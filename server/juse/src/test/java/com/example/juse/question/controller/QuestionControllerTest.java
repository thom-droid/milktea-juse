package com.example.juse.question.controller;

import com.example.juse.JuseApplicationTests;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.notification.service.NotificationService;
import com.example.juse.question.dto.QuestionRequestDto;
import com.example.juse.user.repository.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.plugins.MockMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class QuestionControllerTest extends JuseApplicationTests {

    private final String mappingUrl = "http://localhost:8080/questions";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private UserRepository userRepository;

    @Mock
    NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void givenQuestionDto_whenPostInvoked_thenIsNotificationSaveAndSent() throws Exception {
        // given
        // scenario : user 2 writes question -> user 1 gets notified.
        QuestionRequestDto.Post requestDto = QuestionRequestDto.Post.builder()
                .content("it is too cold outside")
                .boardId(1L)
                .userId(2L)
                .build();

        String requestBody = gson.toJson(requestDto);

        String userName = userRepository.findById(2L).orElseThrow().getEmail();

        accessToken = jwtTokenProvider.generateToken(userName, "ROLE_MEMBER").getAccessToken();

        //when
        ResultActions resultActions = mockMvc.perform(
                post(mappingUrl + "/1")
                        .header("Auth", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        //then
        resultActions.andExpect(status().isCreated());
//        verify(notificationService).send(Mockito.any(Notification.class));

        assertEquals(1, notificationRepository.findAll().size());
    }
}