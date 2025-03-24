package com.arraywork.autumn.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
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

    /** Format time difference */
    public static String formatTimeDifference(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, dateTime);
        long seconds = Math.abs(duration.getSeconds());
        boolean isFuture = dateTime.isAfter(now);

        if (seconds < 60) {
            return isFuture ? seconds + "秒后" : "刚刚";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return isFuture ? minutes + "分钟后" : minutes + "分钟前";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return isFuture ? hours + "小时后" : hours + "小时前";
        } else {
            Period period = Period.between(now.toLocalDate(), dateTime.toLocalDate());
            int years = Math.abs(period.getYears());
            int months = Math.abs(period.getMonths());
            int days = Math.abs(period.getDays());
            if (years > 0) {
                return isFuture ? years + "年后" : years + "年前";
            } else if (months > 0) {
                return isFuture ? months + "月后" : months + "月前";
            } else {
                return isFuture ? days + "天后" : days + "天前";
            }
        }
    }

}