package com.example.juse.notification.service;

import com.example.juse.notification.dto.NotificationResponseDto;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.sse.CustomSse;
import com.example.juse.sse.SseSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomSse<Notification> customSse;


    public void send(Notification notification) {
        Notification savedNotification = save(notification);
        log.info("notification saved. : {} ", savedNotification);
        String userUUID = notification.getReceiver().getUuid();

        for (Map.Entry<String, SseEmitter> entry : customSse.getEmittersByUserUUID(userUUID).entrySet()) {
            NotificationResponseDto responseDto = new NotificationResponseDto(savedNotification);
            SseSource data = new SseSource(userUUID, responseDto, entry.getValue());
            customSse.saveEventCache(entry.getKey(), savedNotification);
            customSse.send(data);
        }

    }

    public SseEmitter createEventStream(String userUUID, String lastEventId) {
        SseSource source = customSse.createSseSourceOf(userUUID, lastEventId);
        customSse.send(source);

        return source.getSseEmitter();
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Page<Notification> getNotificationList(Long userId, Boolean isRead, Pageable pageable) {
        return notificationRepository.findByReceiverIdAndIsRead(userId, isRead, pageable);
    }

    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow();
    }

}
