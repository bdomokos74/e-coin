package com.company;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigurationPackage
//    @EnableJpaRepositories(basePackages = {"com.company.repository"})
@ComponentScan(basePackages = {"com.company.model", "com.company.service", "com.company.repository"})
public class TestConfig {
}