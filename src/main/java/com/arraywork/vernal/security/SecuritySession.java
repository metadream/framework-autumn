package com.arraywork.vernal.security;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

/**
 * Security Session
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecuritySession {

    @Resource
    private HttpSession session;

    /** Get session id */
    public String getId() {
        return session.getId();
    }

    /** Get principal from current session */
    public Principal getPrincipal() {
        Object object = session.getAttribute(session.getId());
        return object instanceof Principal principal ? principal : null;
    }

    /** Set principal to current session */
    public Principal setPrincipal(Principal principal) {
        session.setAttribute(session.getId(), principal);
        return principal;
    }

    /** Destroy current session and principal */
    public void destroy() {
        session.invalidate();
    }

}