package com.example.juse.event;

import com.example.juse.notification.entity.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private Notification event;

    public NotificationEvent(Object source, Notification event) {
        super(source);
        this.event = event;
    }
}
