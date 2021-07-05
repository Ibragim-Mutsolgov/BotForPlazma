package com.tsecho.bots.config.bill;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration

@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "emfBill",
        transactionManagerRef = "tmBill",
        basePackages = {"com.tsecho.bots.repository.bill"}
)
public class DataSourceConfigBill extends ConfigBill {

    public DataSourceConfigBill(BillProps billProps) {
        super(billProps);
    }


    @Bean(name = "dsBill")
    @Primary
    public HikariDataSource dataSourceBill() {
        return new HikariDataSource(this);
    }


    @Bean(name = "emfBill")
   @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBill(@Qualifier("dsBill") HikariDataSource dataSourceBill) {

        return new LocalContainerEntityManagerFactoryBean() {
            {
                setDataSource(dataSourceBill);
                setSchema(dataSourceBill.getSchema());
                setDataSourceClassName(dataSourceBill.getDataSourceClassName());
                setJdbcUrl(dataSourceBill.getJdbcUrl());
                setPersistenceProviderClass(HibernatePersistenceProvider.class);
                setPersistenceUnitName("bill");
                setPackagesToScan("com.tsecho.bots.model.bill");
                setJpaProperties(JPA_MYSQL_PROPERTIES);

            }
        };
    }

    @Bean(name = "tmBill")
    @Primary
    public PlatformTransactionManager transactionManagerPharm(@Qualifier("emfBill") EntityManagerFactory entityManagerFactoryPharm) {
        return new JpaTransactionManager(entityManagerFactoryPharm);
    }
}
