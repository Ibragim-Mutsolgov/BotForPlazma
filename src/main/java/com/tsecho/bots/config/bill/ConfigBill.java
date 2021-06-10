package com.tsecho.bots.config.bill;

import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

public class ConfigBill extends HikariConfig {

        protected final BillProps billProps;

        protected final Properties JPA_MYSQL_PROPERTIES = new Properties() {{
            put("hibernate.dialect", "org.hibernate.dialect.MySQL55Dialect");
            put("show-sql","true");
            put("hibernate.ddl-auto", "none");
        }};

        protected ConfigBill(BillProps billProps) {
            this.billProps = billProps;
            setPoolName(this.billProps.getPoolName());
            setMinimumIdle(this.billProps.getMinimumIdle());
            setMaximumPoolSize(this.billProps.getMaximumPoolSize());
            setIdleTimeout(this.billProps.getIdleTimeout());
            setJdbcUrl(this.billProps.getJdbcUrl());
            setDriverClassName(this.billProps.getDriverClassName());
            setUsername(this.billProps.getUsername());
            setPassword(this.billProps.getPassword());
            setSchema(this.billProps.getShema());
        }
    }
