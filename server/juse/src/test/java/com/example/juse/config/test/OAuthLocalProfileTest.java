package com.example.juse.config.test;

import com.example.juse.security.config.UriProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@SpringBootTest
public class OAuthLocalProfileTest {

    @Autowired
    UriProperties oAuthProperties;

    @Test
    public void shouldProfiledWithLocal_LocalPropertiesReturned() {

        String google = "http://localhost:8080/oauth2/authorization/google";
        String github = "http://localhost:8080/oauth2/authorization/github";
        String redirect = "http://localhost:3000/oauth2";

        assertEquals(google, oAuthProperties.getGoogleRequestUri());
        assertEquals(github, oAuthProperties.getGithubRequestUri());
        assertEquals(redirect, oAuthProperties.getRedirectUrl());

    }

    @Test
    public void shouldProfileWithLocal_RedirectUriBuilt() {

        String redirectUrl = oAuthProperties.getRedirectUrl();
        String token = "token";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("isUser", "0");
        queryParams.add("token", token);

        String redirectUri = UriComponentsBuilder.fromUriString(redirectUrl)
                .pathSegment("redirect")
                .queryParams(queryParams).build().toUriString();

        String expected = "http://localhost:3000/oauth2/redirect?isUser=0&token=token";

        assertEquals(expected, redirectUri);
    }


}
