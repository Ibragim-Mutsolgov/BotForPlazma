package com.tsecho.bots.config.common;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "emfCommon",
        transactionManagerRef = "tmCommon",
        basePackages = {"com.tsecho.bots.repository.common"}
)
public class DataSourceConfigCommon extends ConfigCommon {

    public DataSourceConfigCommon(CommonProps hikariCommonProperties) {
        super(hikariCommonProperties);
    }

    @Bean(name = "dsCommon")
    public HikariDataSource dataSourceCommon() {
        return new HikariDataSource(this);
    }

    @Qualifier("dsCommon")
    @Bean(name = "emfCommon")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryCommon( @Qualifier("dsCommon") HikariDataSource dataSourceCommon) {


        return new LocalContainerEntityManagerFactoryBean() {
            {
                setDataSource(dataSourceCommon);
                setSchema(dataSourceCommon.getSchema());
                setDataSourceClassName(dataSourceCommon.getDataSourceClassName());
                setJdbcUrl(dataSourceCommon.getJdbcUrl());
                setPersistenceProviderClass(HibernatePersistenceProvider.class);
                setPersistenceUnitName("common");
                setPackagesToScan("com.tsecho.bots.model.common");
                setJpaProperties(JPA_POSTGRES_PROPERTIES);

            }
        };
    }
    @Qualifier("dsCommon")
    @Bean(name = "tmCommon")
    public PlatformTransactionManager transactionManagerCommon(EntityManagerFactory entityManagerFactoryCommon) {
        return new JpaTransactionManager(entityManagerFactoryCommon);
    }
}
