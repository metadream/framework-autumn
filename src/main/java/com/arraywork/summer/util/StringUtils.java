package com.arraywork.summer.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * String Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class StringUtils extends org.springframework.util.StringUtils {

    /** Determine it is a positive integer or not */
    public static boolean isInteger(String s) {
        return s != null && s.matches("\\d+");
    }

    /** Determine it is a number or not */
    public static boolean isNumberic(String s) {
        return s != null && s.matches("-?\\d+(\\.\\d+)?");
    }

    /** Build query string from a map (ignores null and empty value) */
    public static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isBlank()) continue;
            if (!sb.isEmpty()) sb.append("&");
            sb.append(String.format("%s=%s",
                URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                URLEncoder.encode(value, StandardCharsets.UTF_8)));
        }
        return sb.toString();
    }

    /** Convert byte array to hexadecimal string */
    public static String toHexadecimal(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) builder.append(0);
            builder.append(hex);
        }
        return builder.toString();
    }

    /** Replace placeholders in the template string by map key */
    public static <T> String compile(String template, Map<String, T> params) {
        org.springframework.util.Assert.notNull(template, "The template to be compiled cannot be null");
        return params == null ? template : params.keySet().stream().reduce(template, (acc, key) ->
            acc.replaceAll("\\{" + key + "\\}", params.get(key).toString())
        );
    }

    /** Replace placeholders in template strings by array index */
    /** Similar to MessageFormat.format(), but does not convert the numeric value */
    public static String compile(String template, Object... params) {
        Assert.notNull(template, "The template to be compiled cannot be null");
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile("\\{\s*\\}");
        Matcher matcher = pattern.matcher(template);

        int i = 0;
        while (matcher.find()) {
            String replacement = i < params.length ? params[i++].toString() : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

}