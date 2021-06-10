package com.tsecho.bots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BotsApplication {

    public static void main(String[] args) {

        SpringApplication.run(BotsApplication.class, args);
    }

}
