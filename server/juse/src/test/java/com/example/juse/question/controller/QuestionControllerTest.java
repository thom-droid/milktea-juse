package com.example.juse.question.controller;

import com.example.juse.TestDBInstance;
import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.event.NotificationEvent;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.notification.service.NotificationService;
import com.example.juse.question.dto.QuestionRequestDto;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import com.example.juse.user.repository.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(TestDBInstance.class)
@RecordApplicationEvents
@AutoConfigureMockMvc
@SpringBootTest
class QuestionControllerTest {

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

    private String accessToken;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    void setup() {
        initJwtToken();
    }

    public void initJwtToken() {

        TokenDto token = jwtTokenProvider.generateToken("test2@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();

    }

    public void setTokenAsBoardWriter() {

        TokenDto token = jwtTokenProvider.generateToken("test1@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();
    }

    @BeforeEach
    public void destroy() {
        notificationRepository.deleteAll();
    }

    @Test
    void givenQuestionDto_whenPostInvoked_thenIsNotificationSaveAndSent() throws Exception {
        // given
        // scenario : user 2 writes question -> user 1 gets notified.
        QuestionRequestDto.Post requestDto = QuestionRequestDto.Post.builder()
                .content("it is too cold outside")
                .boardId(1L)
                .userId(2L)
                .build();

        Notification.Type expectedType = Notification.Type.NEW_REPLY;
        String expectedString = expectedType.getMessage();
        Board board = boardRepository.findById(requestDto.getBoardId()).orElseThrow();
        Long expectedReceiverId = board.getUser().getId();

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
        Notification notification = applicationEvents.stream(NotificationEvent.class).map(NotificationEvent::getEvent).collect(Collectors.toList()).get(0);

        assertEquals(expectedReceiverId, notification.getReceiver().getId());
        assertEquals(expectedType, notification.getType());
        assertEquals(expectedString, notification.getMessage());

    }
}