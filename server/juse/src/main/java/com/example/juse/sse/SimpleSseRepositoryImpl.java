package com.example.juse.sse;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class SimpleSseRepositoryImpl<T> implements SseRepository<T> {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, T> eventCaches = new ConcurrentHashMap<>();

    @Override
    public SseEmitter saveSse(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public T saveEvent(String eventId, T event) {
        eventCaches.put(eventId, event);
        return event;
    }

    @Override
    public Map<String, SseEmitter> findAllSseByUserUUID(String userUUID) {

        return emitters.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userUUID))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, T> findAllEventCachesByUserUUID(String userUUID) {
        return eventCaches.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userUUID))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteEmitterById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmittersByUserUUID(String userUUID) {
        emitters.forEach(
                (key, emitter)  -> {
                    if (key.startsWith(userUUID)) {
                        emitters.remove(key);
                    }
                }
        );

    }
}
