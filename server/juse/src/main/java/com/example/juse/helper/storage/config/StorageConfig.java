package com.example.juse.helper.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class StorageConfig {

    public StorageConfig(StorageProfile storageProfile) {
        this.storageProfile = storageProfile;
    }

    private final StorageProfile storageProfile;

    @Bean
    public S3Client s3Client() {
        log.info("using profile from directory: {}", storageProfile.getProfilePath());
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(ProfileCredentialsProvider.builder()
                        .profileFile(storageProfile.getProfileFile())
                        .build())
                .build();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }
}
