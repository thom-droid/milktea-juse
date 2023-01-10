package com.example.juse.notification.service;

import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.sse.CustomSse;
import com.example.juse.sse.SseSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomSse<Notification> customSse;


    public void send(Notification notification) {
        Notification savedNotification = save(notification);
        String userUUID = notification.getReceiver().getUuid();

        for (Map.Entry<String, SseEmitter> entry : customSse.getEmittersByUserUUID(userUUID).entrySet()) {
            SseSource data = new SseSource(userUUID, savedNotification, entry.getValue());
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
}