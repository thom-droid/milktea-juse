package com.example.juse.notification.controller;

import com.example.juse.notification.service.NotificationService;
import com.example.juse.security.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping(value = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createEventStream(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        String userUUID = principalDetails.getSocialUser().getUser().getUuid();

        return notificationService.createEventStream(userUUID, lastEventId);

    }
}
