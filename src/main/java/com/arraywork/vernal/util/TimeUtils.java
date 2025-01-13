package com.arraywork.vernal.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Date and Time Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/03/11
 */
public class TimeUtils {

    /** Shortcut for thread sleep */
    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Format millisecond timestamp to LocalDateTime instance */
    public static LocalDateTime toLocal(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** Format duration millis to HH:mm:ss */
    public static String formatDuration(double millis) {
        return formatDuration(Double.valueOf(millis).longValue());
    }

    /** Format duration millis to HH:mm:ss */
    public static String formatDuration(long millis) {
        return formatDuration(millis, false);
    }

    /** Format duration millis to HH:mm:ss.SSS */
    public static String formatDuration(long millis, boolean showMs) {
        int ms = (int) (Math.floor(millis) % 1000);
        int seconds = (int) Math.floor(millis / 1000) % 60;
        int minutes = (int) Math.floor(millis / 60000) % 60;
        int hours = (int) Math.floor(millis / 3600000);

        String result = "";
        if (hours > 0) result = String.format("%02d", hours) + ":";
        result += String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        if (showMs) result += "." + String.format("%03d", ms);
        return result;
    }

    /** Format duration seconds to _d _h _m _s */
    public static String formatSeconds(long seconds) {
        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return ((d > 0 ? d + "d " : "") + (h > 0 ? h + "h " : "") + (m > 0 ? m + "m " : "")
            + (s > 0 ? s + "s" : "")).trim();
    }

}