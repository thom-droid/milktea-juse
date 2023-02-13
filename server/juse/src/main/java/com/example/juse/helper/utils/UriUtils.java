package com.example.juse.helper.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class UriUtils {

    public static String getRedirectUriAfterOAuthAuthorized(String redirectUrl, String token, boolean isSocialUserNull) {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (isSocialUserNull) {
            queryParams.add("isUser", "0");
        } else {
            queryParams.add("isUser", "1");
        }

        queryParams.add("token", token);
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .pathSegment("redirect")
                .queryParams(queryParams).build().toUriString();
    }

}
