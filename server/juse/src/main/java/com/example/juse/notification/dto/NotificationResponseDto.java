package com.example.juse.notification.dto;

import com.example.juse.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotificationResponseDto {

    private Notification.Type type;
    private String message;
    private String relatedURL;
    private boolean isRead;


}
