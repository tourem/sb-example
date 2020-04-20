package com.larbotech.batch.config;

import org.junit.Rule;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

@Configuration
@PropertySource("classpath:context-test-batch.properties")
@ImportResource("classpath:test-persistence-context.xml")
@Profile("IT")
public class ConfigurationBatchIT {


}
