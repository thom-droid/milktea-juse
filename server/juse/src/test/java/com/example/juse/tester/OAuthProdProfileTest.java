package com.example.juse.tester;

import com.example.juse.security.config.OAuthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("prod")
@SpringBootTest
public class OAuthProdProfileTest {

    @Autowired
    OAuthProperties oAuthProperties;

    @Test
    public void shouldProfiledWithProd_ProdPropertiesReturned() {

        String google = "https://server.chicken-milktea-juse.com/oauth2/authorization/google";
        String github = "https://server.chicken-milktea-juse.com/oauth2/authorization/github";
        String redirect = "https://chicken-milktea-juse.com/oauth2/redirect";

        assertEquals(google, oAuthProperties.getGoogleRequestUri());
        assertEquals(github, oAuthProperties.getGithubRequestUri());
        assertEquals(redirect, oAuthProperties.getRedirectUrl());

    }


}
