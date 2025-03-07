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

    private Long id;
    private Notification.Type type;
    private String message;
    private String relatedURL;
    private boolean isRead;
    private Long boardId;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.relatedURL = notification.getRelatedURL();
        this.isRead = notification.isRead();
        this.boardId = notification.getBoardId();
    }

}
