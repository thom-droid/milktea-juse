package com.example.juse.notification.service;

import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import com.example.juse.notification.dto.NotificationResponseDto;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.repository.NotificationRepository;
import com.example.juse.sse.CustomSse;
import com.example.juse.sse.SseSource;
import com.example.juse.user.entity.User;
import com.example.juse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
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

    public List<Notification> getNotificationForNav(Long userId){
        return notificationRepository.findTop5ByReceiverIdOrderByCreatedAtDesc(userId);
    }

    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow();
    }

    @Transactional
    public void setNotificationAsRead(User user, Long notificationId) {
        User foundUser = userService.verifyUserById(user.getId());
        List<Notification> notificationList = foundUser.getNotificationList();

        if (notificationList != null && !notificationList.isEmpty()) {
            Notification notification = notificationList.stream()
                    .filter(n -> Objects.equals(n.getId(), notificationId))
                    .findFirst()
                    .orElseThrow(() -> new CustomRuntimeException(ExceptionCode.NOTIFICATION_NOT_FOUND));

            notification.setAsRead();
        } else {
            throw new CustomRuntimeException(ExceptionCode.NOTIFICATION_LIST_NULL_OR_EMPTY);
        }
    }
}
