package com.example.juse.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSse<T> {

    private final SseRepository<T> sseRepository;
    private Object object;

    public SseSource createSseSourceOf(String userUUID, String lastEventId) {

        SseSource infoHolder = new SseSource(userUUID, lastEventId);
        String emitterId = infoHolder.getEmitterId();

        if (lastEventId != null) {
            Map<String, T> cache = sseRepository.findAllEventCachesByUserUUID(lastEventId);

            if (!cache.isEmpty()) {
                infoHolder.setData(cache.get(lastEventId));
                emitterId = lastEventId;
            }
        }

        SseEmitter sseEmitter = sseRepository.saveSse(emitterId, infoHolder.getSseEmitter());
        setCallBackOption(sseEmitter, emitterId);

        return infoHolder;
    }


    private void setCallBackOption(SseEmitter sseEmitter, String emitterId) {
        sseEmitter.onCompletion(() ->
        {
            log.info("Async request is completed.");
            sseRepository.deleteEmitterById(emitterId);
        });

        sseEmitter.onTimeout(() -> {
            log.info("Async request is timed out. Emitter is removed.");
            sseRepository.deleteEmitterById(emitterId);
        });
    }

    public void send(SseSource source) {
        Object data = source.getData();
        String emitterId = source.getEmitterId();
        SseEmitter sseEmitter = source.getSseEmitter();

        try {
            sseEmitter.send(SseEmitter.event().id(emitterId).data(data));
            log.info("sending data: {} ", data);
            log.info("notification information - emitterId : {}, data : {} ", emitterId, data);

        } catch (IOException e) {
            log.debug("event push failed. info : {} / " + "error message : {}", emitterId, e.getMessage());
            sseRepository.deleteEmitterById(emitterId);

        }
    }

    public Map<String, SseEmitter> getEmittersByUserUUID(String userUUID) {
        return sseRepository.findAllSseByUserUUID(userUUID);
    }

    public void saveEventCache(String eventId, T event) {
        sseRepository.saveEvent(eventId, event);
    }

}