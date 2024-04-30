package com.arraywork.springforce;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Server-Sent Event Service
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
@Component
public class SseChannel {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Accept client
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    // Broadcast message to all clients
    public void broadcast(Object object) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(object);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
    }

}