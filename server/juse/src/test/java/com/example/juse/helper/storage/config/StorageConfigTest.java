package com.example.juse.helper.storage.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.profiles.ProfileFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StorageConfigTest {

    private SpringApplication springApplication;

    @BeforeEach
    public void setup() {
        springApplication = new SpringApplication(StorageConfig.class, StorageProfile.class, StorageProfileForLocal.class, StorageProfileForProd.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
    }

    @Test
    public void localProfileTest() {
        ConfigurableApplicationContext context = springApplication.run("--spring.profiles.active=local");
        StorageProfile storageProfile = context.getBean(StorageProfile.class);
        ProfileFile profileFile = storageProfile.getProfileFile();
        String actualPath = storageProfile.getProfilePath();
        String expected = Path.of("C:","Users","thom",".aws","credentials").toString();

        assertNotNull(profileFile);
        assertEquals(expected, actualPath);
    }

}