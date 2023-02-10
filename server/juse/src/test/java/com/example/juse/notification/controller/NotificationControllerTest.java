package com.example.juse.notification.controller;

import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ActiveProfiles({"local"})
@AutoConfigureMockMvc
@SpringBootTest
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void givenURL_thenControllerDoesNotThrowException() throws Exception {

        String url = "http://localhost:8080/event-stream";
        TokenDto tokenDto = jwtTokenProvider.generateToken("test1@gmail.com", "ROLE_MEMBER");
        String token = tokenDto.getAccessToken();

        assertDoesNotThrow(() -> mockMvc.perform(get(url).header("Auth", token)));

    }
}