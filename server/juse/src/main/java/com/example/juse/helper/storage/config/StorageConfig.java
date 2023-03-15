package com.example.juse.helper.storage.config;

import com.example.juse.helper.storage.S3StorageService;
import com.example.juse.helper.storage.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Bean
    public StorageService storageService() {
        return new S3StorageService(s3Client());
    }

}
