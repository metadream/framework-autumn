package com.arraywork.springforce.security;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Http Session Listener
 * There can only be one session for the same account
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    // Listen to session attribute added (that is, Login)
    // and determine whether the current logged-in user is unique
    @Override
    public void attributeAdded(HttpSessionBindingEvent be) {
        Object object = be.getValue();

        if (object instanceof Principal) {
            String id = ((Principal) object).getId();
            HttpSession session = be.getSession();
            ServletContext app = session.getServletContext();
            HttpSession authorizedSession = (HttpSession) app.getAttribute(id);

            if (authorizedSession != null) {
                // Replace the session when the ID is different
                if (!authorizedSession.getId().equals(session.getId())) {
                    authorizedSession.invalidate();
                    app.setAttribute(id, session);
                }
            } else {
                app.setAttribute(id, session);
            }
        }
    }

    // Listen to session destroyed (ex. logout, timeout)
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Object object = session.getAttribute(session.getId());

        // Remove session by id key
        if (object instanceof Principal) {
            String id = ((Principal) object).getId();
            session.getServletContext().removeAttribute(id);
        }
    }

}