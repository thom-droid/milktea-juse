package com.example.juse.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface SseRepository<T> {

    SseEmitter saveSse(String emitterId, SseEmitter sseEmitter);

    T saveEvent(String eventId, T event);

    Map<String, SseEmitter> findAllSseByUserUUID(String userUUID);

    Map<String, T> findAllEventCachesByUserUUID(String userUUID);

    void deleteEmitterById(String emitterId);

    void deleteAllEmittersByUserUUID(String userUUID);
}
