package com.arraywork.springshot.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Numbers Utils
 * @author AiChen
 */
public class Numbers {

    /**
     * 四舍五入保留小数位
     * @param number 需要转换的数字
     * @param digits 最大保留小数位
     * @return
     */
    public static String formatDecimal(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        BigDecimal decimal = BigDecimal.valueOf(number);
        decimal = decimal.setScale(digits, RoundingMode.HALF_UP);
        return decimal.stripTrailingZeros().toPlainString();
    }

    /**
     * 四舍五入保留小数位（不保留小数）
     * @param number 需要转换的数字
     * @return
     */
    public static String formatDecimal(double number) {
        return formatDecimal(number, 0);
    }

    /**
     * 将数字转换为百分比形式
     * @param number 需要转换的数字
     * @param digits 最大保留小数位
     * @return
     */
    public static String formatPercent(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(digits);
        return nf.format(number);
    }

    /**
     * 将数字转换为百分比形式（不保留小数位）
     * @param number
     * @return
     */
    public static String formatPercent(double number) {
        return formatPercent(number, 0);
    }

    /**
     * 将字节数转换为人类可读形式
     * @param bytes  需要转换的字节数
     * @param digits 最大保留小数位
     * @return
     */
    public static String formatBytes(long bytes, int digits) {
        final long KiB = 1024;
        final long MiB = KiB * KiB;
        final long GiB = MiB * KiB;
        final long TiB = GiB * KiB;

        NumberFormat df = NumberFormat.getNumberInstance();
        df.setMaximumFractionDigits(digits);

        if (bytes >= TiB) return df.format(1.0 * bytes / TiB) + " TiB";
        if (bytes >= GiB) return df.format(1.0 * bytes / GiB) + " GiB";
        if (bytes >= MiB) return df.format(1.0 * bytes / MiB) + " MiB";
        if (bytes >= KiB) return df.format(1.0 * bytes / KiB) + " KiB";
        if (bytes > 0) return df.format(bytes) + " B";
        return "0";
    }

    /**
     * 将字节数转换为人类可读形式（最多保留1位小数）
     * @param bytes
     * @return
     */
    public static String formatBytes(long bytes) {
        return formatBytes(bytes, 1);
    }

    /**
     * 将毫秒时间戳转换为本地时间对象
     * @param ms
     * @return
     */
    public static LocalDateTime formatMsTimestamp(long ms) {
        return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将秒数时长转换为日时分秒
     * @param seconds
     * @return
     */
    public static String formatSeconds(long seconds) {
        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return ((d > 0 ? d + "d " : "") + (h > 0 ? h + "h " : "") + (m > 0 ? m + "m " : "")
            + (s > 0 ? s + "s" : "")).trim();
    }

}