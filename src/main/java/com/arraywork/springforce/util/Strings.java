package com.arraywork.springforce.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * String Utilities
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class Strings {

    // Determine it is a positive integer or not
    public static boolean isInteger(String s) {
        return StringUtils.hasText(s) && s.matches("\\d+");
    }

    // Determine it is a number or not
    public static boolean isNumberic(String s) {
        return StringUtils.hasText(s) && s.matches("-?\\d+(\\.\\d+)?");
    }

    // Build query string from a map (ignores null and empty value)
    public static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!StringUtils.hasText(entry.getValue())) continue;
            if (!sb.isEmpty()) sb.append("&");
            sb.append(String.format("%s=%s",
                URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)));
        }
        return sb.toString();
    }

    // Convert byte array to hexadecimal string
    public static String toHexadecimal(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) builder.append(0);
            builder.append(hex);
        }
        return builder.toString();
    }

}