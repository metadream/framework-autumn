package com.arraywork.springforce.channel;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import jakarta.websocket.Session;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Channel Service
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/07/06
 */
@Service
@EnableAsync
@Slf4j
public class ChannelService {

    private static final Map<String, Set<Session>> channels = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 60_000; // 1 minute

    // Broadcast message to all sessions subscribed the channel
    public int broadcast(String channel, Object data) {
        Message message = new Message();
        message.setData(data);

        Assert.notNull(message.getData(), "The field 'data' is required.");
        log.info("Broadcast to channel <{}>: {}", channel, message);

        int result = 0;
        for (Session session : getChannel(channel)) {
            sendMessage(session, message);
            result++;
        }
        return result;
    }

    // Send message to single specified session
    public void sendMessage(Session session, Message message) {
        if (session.isOpen()) session.getAsyncRemote().sendObject(message);
    }

    // Heartbeat ping-pong
    @Async
    public void ping(Session session) {
        try {
            Thread.sleep(TIMEOUT - 5000); // 5 seconds in advance
            if (session.isOpen()) session.getAsyncRemote().sendPing(ByteBuffer.wrap(new byte[0]));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // Subscribe channel
    public void subscribe(String channel, Session session) {
        session.setMaxIdleTimeout(TIMEOUT);
        getChannel(channel).add(session);
    }

    // Unsubscribe channel
    public void unsubscribe(String channel, Session session) {
        getChannel(channel).remove(session);
    }

    // Get session set from specified channel
    // The keyword 'synchronized' is important
    private synchronized Set<Session> getChannel(String channel) {
        Set<Session> sessions = channels.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>());
        return sessions;
    }

}