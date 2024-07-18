package com.arraywork.springforce.channel;

import jakarta.annotation.Resource;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Channel Endpoint
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/07/06
 */
@RestController
@ServerEndpoint(value = "/channel/{channel}", encoders = MessageEncoder.class)
@Slf4j
public class ChannelEndpoint {

    private static ChannelService channelService;

    @Resource
    public void setChannelService(ChannelService channelService) {
        ChannelEndpoint.channelService = channelService;
    }

    @OnOpen
    public void onOpen(@PathParam("channel") String channel, Session session) {
        log.info("Session <{}> subscribes channel <{}>.", session.getId(), channel);
        channelService.subscribe(channel, session);
        channelService.ping(session);
    }

    @OnMessage(maxMessageSize = 1024000) // 1MB
    public void onMessage(@PathParam("channel") String channel, Session session, String message) {
        log.info("Session <{}> sent a message: {}", session.getId(), message);
        channelService.sendMessage(session, new Message("You cannot send message to me."));
    }

    @OnMessage
    public void onPongMessage(Session session, PongMessage message) {
        log.info("Session <{}> sent a pong message: {}", session.getId(), message.getApplicationData());
        channelService.ping(session);
    }

    @OnClose
    public void onClose(@PathParam("channel") String channel, Session session, CloseReason reason) {
        log.info("Session <{}> unsubscribes channel <{}>.", session.getId(), channel);
        channelService.unsubscribe(channel, session);
    }

    @OnError
    public void onError(@PathParam("channel") String channel, Session session, Throwable e) {
        log.error("Session <{}> has an error on channel <{}>.", session.getId(), channel);
        log.error(e.getMessage(), e);
    }

}