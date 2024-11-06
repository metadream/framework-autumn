package com.arraywork.springforce.security;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.springframework.stereotype.Component;

/**
 * Http Session Listener
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component // Replace @Weblistener
public class SessionListener implements HttpSessionListener {

    @Resource
    private SecurityContext context;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        context.addSession(event.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        context.removeSession(event.getSession());
    }

}