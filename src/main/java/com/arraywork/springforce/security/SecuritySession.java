package com.arraywork.springforce.security;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

/**
 * Security Session
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecuritySession {

    @Resource
    private HttpSession session;

    // Get session id
    public String getId() {
        return session.getId();
    }

    // Get principal from current session
    public Principal getPrincipal() {
        Object object = session.getAttribute(session.getId());
        return object instanceof Principal principal ? principal : null;
    }

    // Add principal to current session
    public Principal addPrincipal(Principal principal) {
        session.setAttribute(session.getId(), principal);
        return principal;
    }

    // Destory current session and principal
    public void destory() {
        session.invalidate();
    }

}