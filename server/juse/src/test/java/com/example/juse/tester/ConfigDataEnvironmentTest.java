package com.example.juse.tester;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigDataEnvironmentTest {

    private SpringApplication springApplication;

    @BeforeEach
    void setup() {
        this.springApplication = new SpringApplication(Config.class);
        this.springApplication.setWebApplicationType(WebApplicationType.NONE);
    }

    @Test
    void runLoadsApplication() {
        ConfigurableApplicationContext context = this.springApplication.run();
        String property = context.getEnvironment().getProperty("cors.allowed-origin");
        assertEquals("http://localhost:3000", property);
    }

    @Test
    void runLoadsWithLocalProfile() {
        ConfigurableApplicationContext context = this.springApplication.run("--spring.profiles.active=local");
        String property = context.getEnvironment().getProperty("oauth.redirectUri");
        assertEquals("http://localhost:3000/oauth2", property);

    }


    @Configuration(proxyBeanMethods = false)
    static class Config {

    }
}
