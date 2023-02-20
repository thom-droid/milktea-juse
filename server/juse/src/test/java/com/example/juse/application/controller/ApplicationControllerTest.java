package com.example.juse.application.controller;

import com.example.juse.TestDBInstance;
import com.example.juse.application.entity.Application;
import com.example.juse.application.repository.ApplicationRepository;
import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.event.NotificationEvent;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RecordApplicationEvents
@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(TestDBInstance.class)
@SpringBootTest()
@AutoConfigureMockMvc
class ApplicationControllerTest {

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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ApplicationEvents applicationEvents;

    protected String accessToken;

    private final String requestMappingUrl = "http://localhost:8080";

    @BeforeEach
    void setup() {
        initJwtToken();
        destroy();
    }
    public void initJwtToken() {

        TokenDto token = jwtTokenProvider.generateToken("test2@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();

    }

    public void destroy() {
        applicationRepository.deleteAll();
        notificationRepository.deleteAll();

    }

    public void setTokenAsBoardWriter() {

        TokenDto token = jwtTokenProvider.generateToken("test1@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();
    }

    @Test
    void givenApplicationRequestDto_whenPosted_thenApplicationAndNotificationIsCreated() throws Exception {
        //given
        long boardId = 1L;
        String position = "backend";
        Notification.Type expectedType = Notification.Type.NEW_APPLICATION;
        String expectedMsg = expectedType.getMessage();

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

        applicationEvents.stream(NotificationEvent.class).forEach(notificationEvent-> {
            Notification notification = notificationEvent.getEvent();
            assertEquals(expectedType, notification.getType());
            assertEquals(expectedMsg, notification.getType().getMessage());
        });

    }


    @Test
    void givenApplicationId_whenAccept_thenDoesStatusUpdateAndNotificationIsSent() throws Exception {

        //given
        setTokenAsBoardWriter();
        Notification.Type expectedType = Notification.Type.APPLICATION_ACCEPT;
        String expectedMessage = expectedType.getMessage();

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

        List<Notification> notificationList = applicationEvents.stream(NotificationEvent.class).map(NotificationEvent::getEvent).collect(Collectors.toList());
        Notification notification = notificationList.get(0);

        assertEquals(1, notificationList.size());
        assertEquals(expectedType, notification.getType());
        assertEquals(expectedMessage, notification.getType().getMessage());
        assertEquals(updated.getUser().getId(), notification.getReceiver().getId());

    }

    @Test
    void givenApplicationId_whenDeny_thenDoesStatusUpdatedAndNotificationIsSent() throws Exception {
        //given
        setTokenAsBoardWriter();

        Notification.Type expectedType = Notification.Type.APPLICATION_DENIED;
        String expectedMessage = expectedType.getMessage();

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

        List<Notification> notificationList = applicationEvents.stream(NotificationEvent.class).map(NotificationEvent::getEvent).collect(Collectors.toList());
        Notification notification = notificationList.get(0);

        assertEquals(1, notificationList.size());
        assertEquals(expectedType, notification.getType());
        assertEquals(expectedMessage, notification.getType().getMessage());
        assertEquals(board.getUrl(), notification.getRelatedURL());

    }
}