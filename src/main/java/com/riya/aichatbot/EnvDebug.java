package com.riya.aichatbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvDebug {

    @Bean
    CommandLineRunner debug(Environment env) {
        return args -> {
            System.out.println("POSTGRES_USER = " + env.getProperty("POSTGRES_USER"));
            System.out.println("POSTGRES_PASSWORD = " + env.getProperty("POSTGRES_PASSWORD"));
            System.out.println("spring.datasource.username = " + env.getProperty("spring.datasource.username"));
            System.out.println("spring.datasource.password = " + env.getProperty("spring.datasource.password"));
        };
    }
}