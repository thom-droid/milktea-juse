package com.example.juse.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OAuthProperties {

    @Value("${oauth.requestUri.google}")
    String googleRequestUri;

    @Value("${oauth.requestUri.github}")
    String githubRequestUri;

    @Value("${oauth.redirectUri}")
    String redirectUrl;

}
