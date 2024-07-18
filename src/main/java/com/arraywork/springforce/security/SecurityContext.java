package com.arraywork.springforce.security;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Security Context
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecurityContext {

    @Autowired
    private HttpSession session;

    // Get principal
    public Principal getPrincipal() {
        Object object = session.getAttribute(session.getId());
        if (object instanceof Principal) {
            return (Principal) object;
        }
        return null;
    }

    // Authorize principal
    public void authorize(Principal principal) {
        session.setAttribute(session.getId(), principal);
    }

    // Destory principal
    public void destory() {
        session.invalidate();
    }

    // Get all principals online
    public List<Principal> getPrincipals() {
        List<Principal> principals = new ArrayList<>();
        ServletContext app = session.getServletContext();
        Enumeration<String> e = app.getAttributeNames();

        while (e.hasMoreElements()) {
            Object object = app.getAttribute(e.nextElement());
            if (object instanceof HttpSession) {
                try {
                    HttpSession session = (HttpSession) object;
                    principals.add((Principal) session.getAttribute(session.getId()));
                } catch (IllegalStateException ex) {
                    // ignored
                }
            }
        }
        return principals;
    }

}