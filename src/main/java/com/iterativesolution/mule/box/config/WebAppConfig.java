package com.iterativesolution.mule.box.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("com.iterativesolution.mule.box")
@EnableJpaRepositories("com.iterativesolution.mule.box.repository")
public class WebAppConfig {

}
