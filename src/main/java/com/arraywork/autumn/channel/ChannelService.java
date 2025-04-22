package com.arraywork.autumn.channel;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import jakarta.websocket.Session;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Channel Service of WebSocket
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/07/06
 */
@Service
@Slf4j
public class ChannelService {

    private static final Map<String, Set<Session>> channels = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 60_000; // 1 minute

    private OnOpenCallback onOpenCallback;
    private OnMessageCallback onMessageCallback;

    public void onOpen(OnOpenCallback callback) {
        this.onOpenCallback = callback;
    }

    public void onMessage(OnMessageCallback callback) {
        this.onMessageCallback = callback;
    }

    /** Subscribe channel */
    public void subscribe(String channel, Session session) {
        session.setMaxIdleTimeout(TIMEOUT);
        getChannel(channel).add(session);

        if (onOpenCallback != null) {
            onOpenCallback.handle(channel, session);
        }
    }

    /** Unsubscribe channel */
    public void unsubscribe(String channel, Session session) {
        getChannel(channel).remove(session);
    }

    /** Receive message and callback */
    public void receiveMessage(String channel, Session session, String message) {
        if (onMessageCallback != null) {
            onMessageCallback.handle(channel, session, message);
        }
    }

    /** Broadcast message to all sessions subscribed the channel with default event */
    public int broadcast(String channel, Object data) {
        return broadcast(channel, null, data);
    }

    /** Broadcast message to all sessions subscribed the channel */
    public int broadcast(String channel, String event, Object data) {
        Assert.notNull(data, "The field 'data' is required.");
        log.info("Broadcast to channel <{}>: {} - {}", channel, event, data);

        int result = 0;
        for (Session session : getChannel(channel)) {
            sendMessage(session, event, data);
            result++;
        }
        return result;
    }

    /** Send message to single specified session with data */
    public void sendMessage(Session session, Object data) {
        ChannelMessage message = new ChannelMessage();
        message.setData(data);
        sendMessage(session, message);
    }

    /** Send message to single specified session with data */
    public void sendMessage(Session session, String event, Object data) {
        ChannelMessage message = new ChannelMessage();
        message.setEvent(event);
        message.setData(data);
        sendMessage(session, message);
    }

    /** Send message to single specified session with message */
    public void sendMessage(Session session, ChannelMessage message) {
        if (session.isOpen()) session.getAsyncRemote().sendObject(message);
    }

    /** Heartbeat ping-pong */
    @Async
    public void ping(Session session) {
        try {
            Thread.sleep(TIMEOUT - 5000); // 5 seconds in advance
            if (session.isOpen()) session.getAsyncRemote().sendPing(ByteBuffer.wrap(new byte[0]));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Get session set from specified channel
     * The keyword 'synchronized' is necessary
     */
    private synchronized Set<Session> getChannel(String channel) {
        return channels.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>());
    }

    @FunctionalInterface
    public interface OnOpenCallback {
        void handle(String channel, Session session);
    }

    @FunctionalInterface
    public interface OnMessageCallback {
        void handle(String channel, Session session, String message);
    }

}