package com.example.juse.notification.controller;

import com.example.juse.config.IntegrationTestDBInstance;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles({"test"})
@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(IntegrationTestDBInstance.class)
@AutoConfigureMockMvc
@SpringBootTest
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

//    @Test
//    void givenURL_thenControllerDoesNotThrowException() throws Exception {
//
//        String url = "http://localhost:8080/notification/event-stream";
//        TokenDto tokenDto = jwtTokenProvider.generateToken("test1@gmail.com", "ROLE_MEMBER");
//        String token = tokenDto.getAccessToken();
//
//        assertDoesNotThrow(() -> mockMvc.perform(get(url + "/{uuid}", ).header("Auth", token)));
//
//    }

    @Test
    void getNotificationListForNav() throws Exception {

        //given
        User user = userRepository.findByEmail("test1@gmail.com");
        String url = "http://localhost:8080/notifications/nav";
        String token = jwtTokenProvider.generateToken(user.getEmail(), "ROLE_MEMBER").getAccessToken();

        for (int i = 0; i < 10; i++) {
            Notification notification = Notification.of(Notification.Type.NEW_APPLICATION, user, null);
            notificationRepository.save(notification);
        }

        //when
        ResultActions resultActions = mockMvc.perform(get(url).header("Auth", token));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(print());
    }
}