package com.example.juse.event;

import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification = event.getEvent();
        log.info("notification event listener publishes an event.");
        notificationService.send(notification);
    }

}
