package com.tsecho.bots.config.common;

import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

public class ConfigCommon extends HikariConfig {

        protected final CommonProps commonProperties;

        protected final Properties JPA_POSTGRES_PROPERTIES = new Properties() {{
            put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
            put("show-sql","true");
            put("hibernate.ddl-auto", "none");
        }};

        protected ConfigCommon(CommonProps commonProperties) {
            this.commonProperties = commonProperties;
            setPoolName(this.commonProperties.getPoolName());
            setMinimumIdle(this.commonProperties.getMinimumIdle());
            setMaximumPoolSize(this.commonProperties.getMaximumPoolSize());
            setIdleTimeout(this.commonProperties.getIdleTimeout());
            setJdbcUrl(this.commonProperties.getJdbcUrl());
            setDriverClassName(this.commonProperties.getDriverClassName());
            setUsername(this.commonProperties.getUsername());
            setPassword(this.commonProperties.getPassword());
        }
    }
