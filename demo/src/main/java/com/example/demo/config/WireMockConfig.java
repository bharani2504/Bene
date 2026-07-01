package com.example.demo.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.wiremock.enabled", havingValue = "true")
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());
        return new WireMockServer(
                WireMockConfiguration.options()
                        .port(8085)
                        .usingFilesUnderClasspath("wiremock")
        );
    }
}
