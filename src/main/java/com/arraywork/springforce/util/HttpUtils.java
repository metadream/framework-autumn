package com.arraywork.springforce.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP Utilities
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
public class HttpUtils {

    // Get wildcard parameter
    public static String getWildcard(HttpServletRequest request, String prefix) {
        String wildcard = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return wildcard.replaceAll("^" + prefix, "");
    }

    // Get client request ip address
    public String getIpAddress(HttpServletRequest request) {
        final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };

        for (String header : IP_HEADER_CANDIDATES) {
            String address = request.getHeader(header);
            if (address != null && !address.isBlank() && !"unknown".equalsIgnoreCase(address)) {
                return address.split("\\s*,\\s*")[0];
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

    // Determine address is internal or not
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