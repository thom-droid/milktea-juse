package com.example.juse.notification.service;

import com.example.juse.TestDBInstance;
import com.example.juse.notification.entity.Notification;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(TestDBInstance.class)
@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenNotification_whenSendInvoked_thenNotificationDoesNotThrow() {

        User receiver = userRepository.findByEmail("test1@gmail.com");
        Notification notification = Notification.of(Notification.Type.NEW_REPLY, receiver, "http://localhost:8080/board/1");

        assertDoesNotThrow(() -> notificationService.send(notification));

    }

}