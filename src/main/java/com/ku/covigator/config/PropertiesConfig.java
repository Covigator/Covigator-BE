package com.ku.covigator.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("com.ku.covigator.config.properties")
public class PropertiesConfig {
}
