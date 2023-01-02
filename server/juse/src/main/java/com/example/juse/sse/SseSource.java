package com.example.juse.sse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@AllArgsConstructor
@Getter
public class SseInfoHolder {

    private static final long TIME_OUT = 60 * 60 * 1000L;

    private final String userUUID;
    private final String lastEventId;
    private String emitterId;
    private SseEmitter sseEmitter;

    @Setter
    private Object data;

    private String buildEmitterId(String userUUID) {
        return userUUID + "-" + System.currentTimeMillis();
    }

    public SseInfoHolder(String userUUID, String lastEventId) {
        this.userUUID = userUUID;
        this.lastEventId = lastEventId;
        this.emitterId = buildEmitterId(userUUID);
        this.data = "connection made for user [" + userUUID + "] is made";
        this.sseEmitter = new SseEmitter(TIME_OUT);
    }

    public SseInfoHolder(String emitterId, Object data, SseEmitter sseEmitter) {
        this(null, null);
        this.emitterId = emitterId;
        this.data = data;
        this.sseEmitter = sseEmitter;
    }


}
