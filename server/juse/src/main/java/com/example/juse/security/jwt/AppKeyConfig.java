package com.example.juse.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class AppKeyConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public String encodedBase64SecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Bean
    public SecretKeyHolder secretKeyHolder() {
        final String encodedKey = this.encodedBase64SecretKey();
        return new DefaultKeyHolder(encodedKey);
    }
}
