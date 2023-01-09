package com.example.juse.notification.service;

import com.example.juse.JuseApplicationTests;
import com.example.juse.notification.entity.Notification;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest extends JuseApplicationTests {

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