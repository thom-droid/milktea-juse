package com.example.juse.application.controller;

import com.example.juse.JuseApplicationTests;
import com.example.juse.application.entity.Application;
import com.example.juse.application.repository.ApplicationRepository;
import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ApplicationControllerTest extends JuseApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Gson gson;


    private final String requestMappingUrl = "http://localhost:8080";

    @BeforeEach
    public void destroy() {
        applicationRepository.deleteAll();
        notificationRepository.deleteAll();

    }

    @Test
    void givenApplicationRequestDto_whenPosted_thenApplicationAndNotificationIsCreated() throws Exception {
        //given
        long boardId = 1L;
        String position = "backend";

        //when
        ResultActions resultActions =
                mockMvc
                        .perform(
                                post(requestMappingUrl + "/apply/board/" + boardId)
                                        .header("Auth", accessToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("position", position)
                        );

        resultActions.andExpect(status().isCreated());

        assertTrue(applicationRepository.findByUserIdAndBoardId(2L, boardId).isPresent());

        Notification.Type notificationType = notificationRepository.findAll().get(0).getType();
        assertEquals(Notification.Type.NEW_APPLICATION, notificationType);

    }


    @Test
    void givenApplicationId_whenAccept_thenDoesStatusUpdateAndNotificationIsSent() throws Exception {

        //given
        setTokenAsBoardWriter();

        Board board = boardRepository.findById(1L).orElseThrow();
        User user = userRepository.findByEmail("test2@gmail.com");
        Application application = Application.builder()
                .board(board)
                .user(user)
                .position("backend")
                .build();

        Application savedApplication = applicationRepository.save(application);
        long applicationId = savedApplication.getId();

        //when
        ResultActions resultActions = mockMvc
                .perform(
                        patch(
                                requestMappingUrl + "/accept/application/" + applicationId)
                                .header("Auth", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        //then
        resultActions.andExpect(status().isOk());

        Application updated = applicationRepository.findById(applicationId).orElseThrow();

        Thread.sleep(3000);
        Notification notification = notificationRepository.findAll().get(0);

        assertEquals(updated.getStatus(), Application.Status.ACCEPTED);
        assertEquals(Notification.Type.APPLICATION_ACCEPT, notification.getType());
        assertEquals(user.getId(), notification.getReceiver().getId());
        assertEquals(board.getUrl(), notification.getRelatedURL());

    }

    @Test
    void givenApplicationId_whenDeny_thenDoesStatusUpdatedAndNotificationIsSent() throws Exception {
        //given
        setTokenAsBoardWriter();

        Board board = boardRepository.findById(1L).orElseThrow();
        User user = userRepository.findByEmail("test2@gmail.com");
        Application application = Application.builder()
                .board(board)
                .user(user)
                .position("backend")
                .build();

        Application savedApplication = applicationRepository.save(application);
        long applicationId = savedApplication.getId();

        //when
        ResultActions resultActions = mockMvc
                .perform(
                        patch(
                                requestMappingUrl + "/deny/application/" + applicationId)
                                .header("Auth", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        //then
        resultActions.andExpect(status().isOk());

        Application updated = applicationRepository.findById(applicationId).orElseThrow();

        Notification notification = notificationRepository.findAll().get(0);

        assertEquals(updated.getStatus(), Application.Status.DENIED);
        assertEquals(Notification.Type.APPLICATION_DENIED, notification.getType());
        assertEquals(user.getId(), notification.getReceiver().getId());
        assertEquals(board.getUrl(), notification.getRelatedURL());

    }
}