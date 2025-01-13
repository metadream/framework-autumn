package com.arraywork.vernal.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

/**
 * Security Context
 *
 * @author Marco
 * @version 0.1.0
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecurityContext {

    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    // Add session
    public HttpSession addSession(HttpSession session) {
        return sessions.put(session.getId(), session);
    }

    // Remove session
    public HttpSession removeSession(HttpSession session) {
        return sessions.remove(session.getId());
    }

    // Get session by specified id
    public HttpSession getSession(String id) {
        return sessions.get(id);
    }

    // Get all sessions in the application
    public List<HttpSession> getSessions() {
        return sessions.values().stream().toList();
    }

    // Get all sessions by principal (in the case of multiple logins for one user)
    public List<HttpSession> getSessions(Principal principal) {
        return sessions.values().stream().filter(session -> {
            Object object = session.getAttribute(session.getId());
            return object instanceof Principal user && user.getId().equals(principal.getId());
        }).toList();
    }

    // Get all principals online
    public List<Principal> getPrincipals() {
        List<Principal> principals = new ArrayList<>();
        for (HttpSession session : sessions.values()) {
            Object object = session.getAttribute(session.getId());
            if (object instanceof Principal principal) {
                principals.add(principal);
            }
        }
        return principals;
    }

}