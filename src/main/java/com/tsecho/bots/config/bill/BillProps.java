package com.tsecho.bots.config.bill;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ds.bill")
@Data
public class BillProps {

    private String poolName;

    private int minimumIdle;

    private int maximumPoolSize;

    private int idleTimeout;

    private String jdbcUrl;

    private String driverClassName;

    private String username;

    private String password;

    private String shema;

}
