package com.arraywork.springforce.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;

    // Get principal
    public Principal getPrincipal() {
        Object object = session.getAttribute(session.getId());
        if (object != null && object instanceof Principal) {
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
            if (object != null && object instanceof HttpSession) {
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

    // Get client request ip address
    public String getIpAddress() {
        final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };

        for (String header : IP_HEADER_CANDIDATES) {
            String address = request.getHeader(header);
            if (address != null && !address.isBlank() && !"unknown".equalsIgnoreCase(address)) {
                return address.split(",")[0];
            }
        }

        String ipAddress = request.getRemoteAddr();
        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            try {
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return ipAddress;
    }

}