package com.arraywork.springforce;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.arraywork.springforce.util.KeyGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-Sent Event Service
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
@Component
@Slf4j
public class SseChannel {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe() {
        String id = KeyGenerator.nanoId();
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onTimeout(() -> {
            log.warn("Channel '{}' is timeout.", id);
            emitters.remove(id);
        });
        emitter.onCompletion(() -> {
            log.warn("Channel '{}' is completed.", id);
            emitters.remove(id);
        });
        emitter.onError(e -> {
            log.error("Channel '{}' error: ", id);
            emitters.remove(id);
        });
        emitters.put(id, emitter);
        return emitter;
    }

    public void publish(String id, Object object) {
        SseEmitter emitter = emitters.get(id);
        if (emitter != null) {
            try {
                emitter.send(object);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }

    // TODO foreach remove元素风险
    public void publish(Object object) {
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(object);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
    }

}