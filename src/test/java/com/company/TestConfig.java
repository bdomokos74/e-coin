package com.company;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@Configuration
@AutoConfigurationPackage
//    @EnableJpaRepositories(basePackages = {"com.company.repository"})
@ComponentScan(basePackages = {"com.company.model", "com.company.service", "com.company.repository"})
public class TestConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
        final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("name", "testWallet");
        properties.setProperty("peer.port", "1234");
        properties.setProperty("server.port", "5678");
        pspc.setProperties(properties);
        return pspc;
    }
}