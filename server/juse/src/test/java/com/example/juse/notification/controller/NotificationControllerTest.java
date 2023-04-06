package com.example.juse.notification.controller;

import com.example.juse.board.entity.Board;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    void getNotificationListForNav() throws Exception {

        //given
        User user = userRepository.findByEmail("test1@gmail.com");
        String url = "http://localhost:8080/notifications/nav";
        String token = jwtTokenProvider.generateToken(user.getEmail(), "ROLE_MEMBER").getAccessToken();
        Board board = Board.builder().id(1L).user(user).title("board1").build();

        for (int i = 0; i < 10; i++) {
            Notification notification = Notification.of(Notification.Type.NEW_APPLICATION, user, board);
            notificationRepository.save(notification);
        }

        //when
        ResultActions resultActions = mockMvc.perform(get(url).header("Auth", token));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(print());
    }

    @Test
    void updateNotificationAsRead() throws Exception {
        //given
        User user = userRepository.findByEmail("test1@gmail.com");
        String url = "http://localhost:8080/notifications";
        String token = jwtTokenProvider.generateToken(user.getEmail(), "ROLE_MEMBER").getAccessToken();
        Long notificationId = 1L;
        Board board = Board.builder().id(1L).user(user).title("board1").build();
        Notification notification = Notification.of(Notification.Type.APPLICATION_ACCEPT, user, board);
        notificationRepository.save(notification);

        //when
        ResultActions resultActions = mockMvc.perform(
                patch(url +"/{notificationId}", notificationId).header("Auth", token));

        //then
        resultActions.andExpect(status().isNoContent());
        Optional<Notification> optionalNotification = notificationRepository.findById(1L);
        assertTrue(optionalNotification.isPresent());
        assertTrue(optionalNotification.get().isRead());
    }
}