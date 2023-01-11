package com.example.juse.application.controller;

import com.example.juse.JuseApplicationTests;
import com.example.juse.application.repository.ApplicationRepository;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
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
    Gson gson;

    private final String requestMappingUrl = "http://localhost:8080/applications";

    @Test
    void givenApplicationRequestDto_whenPosted_thenApplicationAndNotificationIsCreated() throws Exception {
        //given
        long boardId = 1L;
        String position = "backend";

        //when
        ResultActions resultActions =
                mockMvc
                        .perform(
                                post(requestMappingUrl + "/" + boardId)
                                        .header("Auth", accessToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("position", position)
                        );

        resultActions.andExpect(status().isCreated());

        assertTrue(applicationRepository.findByUserIdAndBoardId(2L, boardId).isPresent());

        Notification.Type notificationType = notificationRepository.findAll().get(0).getType();
        assertEquals(Notification.Type.NEW_APPLICATION, notificationType);

    }


}