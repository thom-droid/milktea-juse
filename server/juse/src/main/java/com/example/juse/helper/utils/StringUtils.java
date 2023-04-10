package com.example.juse.helper.utils;

import com.example.juse.board.entity.Board;
import com.example.juse.notification.entity.Notification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

public class StringUtils {

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

    public static String createUniqueAndRegulatedFileName(String fileName) {
        String regulatedFileName = regulate(fileName);
        return UUID.randomUUID() + "-" + regulatedFileName;
    }

    public static String buildLocation(String host, String fileName, String... pathSegment) {
        return UriComponentsBuilder.newInstance().scheme("https").host(host).pathSegment(pathSegment).path("/{fileName}").build(fileName).toString();
    }

    private static String regulate(String str) {
        String extension = str.substring(str.lastIndexOf("."));
        String name = str.substring(0, str.indexOf(extension));
        String regex = "[^A-Za-z0-9]";
        String regulated = name.replaceAll(regex, "");
        return regulated + extension;

    }

    public static String createBoardRedirectUri(String url, Long id) {
        return UriComponentsBuilder.fromHttpUrl(url).pathSegment("boards", "{id}").build(id).toString();
    }
}
