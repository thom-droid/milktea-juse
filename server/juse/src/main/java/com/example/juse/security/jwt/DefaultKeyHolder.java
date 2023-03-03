package com.example.juse.security.jwt;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultKeyHolder implements SecretKeyHolder {

    private final String secretKey;

    public String getSecretKey() {
        return this.secretKey;
    }
}
