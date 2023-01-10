package com.example.juse.notification.dto;

import com.example.juse.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponseDto {

    private Notification.Type type;
    private String message;
    private String relatedURL;
    private boolean isRead;

    public NotificationResponseDto(Notification notification) {
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.relatedURL = notification.getRelatedURL();
        this.isRead = notification.isRead();
    }

}
