package com.arraywork.summer.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
@Slf4j
public class HttpUtils {

    /** Get origin */
    public static String getOrigin(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder origin = new StringBuilder();
        origin.append(scheme).append("://").append(serverName);
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            origin.append(":").append(serverPort);
        }
        return origin.toString();
    }

    /** Get wildcard parameter */
    public static String getWildcard(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return path.replace(pattern.replace("/**", ""), "");
    }

    /** Is mobile agent */
    public static boolean isMobileAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent != null) {
            String mobileAgents = "Android|iPhone|iPad|iPod|Windows Phone|blackberry|ucweb";
            Pattern pattern = Pattern.compile(".*(" + mobileAgents + ").*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userAgent);
            return matcher.matches();
        }
        return false;
    }

    /** Create cookie */
    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    /** Get cookie */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && StringUtils.hasText(name)) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /** Delete cookie */
    public static Cookie deleteCookie(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (cookie != null) {
            cookie.setValue(null);
            cookie.setMaxAge(0);
        }
        return cookie;
    }

    /** Determine address is internal or not */
    public static boolean isInternalIp(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            // Loopback address of IPv4 or IPv6
            if (inetAddress.isLoopbackAddress()) return true;
            // Private address of IPv4 or unique local address of IPv6
            if (inetAddress.isSiteLocalAddress()) return true;
            // Link-local address of IPv6
            if (inetAddress.isLinkLocalAddress()) return true;

            // Check private address of IPv4
            if (inetAddress.getAddress().length == 4) {
                byte[] addressBytes = inetAddress.getAddress();
                // Convert to an unsigned integer
                int firstByte = addressBytes[0] & 0xFF;
                int secondByte = addressBytes[1] & 0xFF;
                // 10.0.0.0/8
                if (firstByte == 10) return true;
                // 172.16.0.0/12
                if (firstByte == 172 && (secondByte >= 16 && secondByte <= 31)) return true;
                // 192.168.0.0/16
                if (firstByte == 192 && secondByte == 168) return true;
                // 169.254.0.0/16 (Link-local address of IPv6)
                if (firstByte == 169 && secondByte == 254) return true;
            }
            return false;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /** Get client request ip address */
    public static String getIpAddress(HttpServletRequest request) {
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
                log.error(e.getMessage(), e);
            }
        }
        return ipAddress;
    }

}