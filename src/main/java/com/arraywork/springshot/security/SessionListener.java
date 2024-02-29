package com.arraywork.springshot.security;

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
 * @created 2024/02/29
 */
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    // Listen to session attribute added (that is, Login)
    // and determine whether the current logged-in user is unique
    @Override
    public void attributeAdded(HttpSessionBindingEvent be) {
        Object object = be.getValue();

        if (object != null && object instanceof Principal) {
            String username = ((Principal) object).getUsername();
            HttpSession session = be.getSession();
            ServletContext app = session.getServletContext();
            HttpSession authorizedSession = (HttpSession) app.getAttribute(username);

            if (authorizedSession != null) {
                if (!authorizedSession.getId().equals(session.getId())) {
                    authorizedSession.invalidate();
                    app.setAttribute(username, session);
                }
            } else {
                app.setAttribute(username, session);
            }
        }
    }

    // Listen to session destroyed (ex. logout, timeout)
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Object object = session.getAttribute(session.getId());

        if (object != null && object instanceof Principal) {
            String username = ((Principal) object).getUsername();
            session.getServletContext().removeAttribute(username);
        }
    }

}