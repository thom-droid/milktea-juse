package com.example.juse.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UriProperties {

    @Value("${oauth.requestUri.google}")
    private String googleRequestUri;

    @Value("${oauth.requestUri.github}")
    private String githubRequestUri;

    @Value("${oauth.redirectUri}")
    private String redirectUrl;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;
}
