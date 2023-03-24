package com.example.juse.helper.storage.config;

import com.example.juse.helper.storage.S3StorageService;
import com.example.juse.helper.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext
class StorageConfigTest {

    private SpringApplication springApplication;

    @BeforeEach
    public void setup() {
        springApplication = new SpringApplication(
                StorageConfig.class, StorageProfile.class, StorageProfileForLocal.class, StorageProfileForProd.class,
                S3StorageService.class, StorageService.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
    }

    @Test
    public void localProfileTest() {
        ConfigurableApplicationContext context = springApplication.run("--spring.profiles.active=local");
        assertDoesNotThrow(()-> context.getBean(StorageProfileForLocal.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(StorageProfileForProd.class));
    }

    @Test
    public void storageServiceBeanTest() {
        ConfigurableApplicationContext context = springApplication.run();
        assertDoesNotThrow(() -> context.getBean(StorageService.class));
    }

}