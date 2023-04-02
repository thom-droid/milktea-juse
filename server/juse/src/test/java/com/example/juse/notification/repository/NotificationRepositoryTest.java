package com.example.juse.notification.repository;

import com.example.juse.config.JpaTestDBInstance;
import com.example.juse.notification.entity.Notification;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaTestDBInstance.class)
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findTopFiveByReceiverIdOrderByCreatedAt() {
        User user = userRepository.findByEmail("test1@gmail.com");

        for (int i = 0; i < 10; i++) {
            Notification notification = Notification.of(Notification.Type.NEW_APPLICATION, user, null);
            notificationRepository.save(notification);
        }

        List<Notification> list = notificationRepository.findAll();

        assertEquals(10, list.size());

        Notification notification1 = list.get(9);
        notification1.setRead(true);
        notificationRepository.save(notification1);

        List<Notification> list2 = notificationRepository.findTop5ByReceiverIdOrderByCreatedAtDesc(user.getId());

        assertEquals(5, list2.size());
        System.out.println(list2.get(0).getCreatedAt());
        System.out.println(list2.get(1).getCreatedAt());
//        assertTrue(list2.get(0).getCreatedAt().isAfter(list2.get(1).getCreatedAt()));
        assertEquals(9, (long) list2.get(0).getId());

        for (Notification n : list2) {
            assertFalse(n.isRead());
        }
    }
}