package com.arraywork.springhood.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP Utilities
 * @author AiChen
 * @created 2023/01/28
 */
public class HttpUtils {

    private static final String[] ADDR_HEADERS = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    /**
     * Get wildcard parameter
     * @param request
     * @param prefix
     * @return
     */
    public static String getWildcard(HttpServletRequest request, String prefix) {
        String wildcard = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return wildcard.replaceAll("^" + prefix, "");
    }

    /**
     * Get request ip address
     * @param request
     * @return
     */
    public static String getRequestAddr(HttpServletRequest request) {
        for (String key : ADDR_HEADERS) {
            String value = request.getHeader(key);
            if (value == null || value.isEmpty()) continue;
            return value.split("\\s*,\\s*")[0];
        }
        return request.getRemoteAddr();
    }

    /**
     * Determine address is internal
     * @param addr
     * @return
     */
    public static boolean isInternalAddr(String addr) {
        if (!Strings.isBlank(addr)) {
            if ("127.0.0.1".equals(addr) || "localhost".equals(addr)) return true;
            String[] addrs = addr.split("\\.");
            if (addrs.length == 4) {
                int s1 = Integer.parseInt(addrs[0]);
                int s2 = Integer.parseInt(addrs[1]);
                // Class A 10.0.0.0-10.255.255.255
                if (s1 == 10) return true;
                // Class B 172.16.0.0-172.31.255.255
                if (s1 == 172 && (s2 >= 16 || s2 <= 31)) return true;
                // Class C 192.168.0.0-192.168.255.255
                if (s1 == 192 && s2 == 168) return true;
            }
        }
        return false;
    }

}