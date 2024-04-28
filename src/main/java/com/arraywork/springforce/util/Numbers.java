package com.arraywork.springforce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Numbers Utilities
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/03/11
 */
public class Numbers {

    // Rounding number without decimal
    public static String formatDecimal(double number) {
        return formatDecimal(number, 0);
    }

    // Rounding number with decimal digits
    public static String formatDecimal(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        BigDecimal decimal = BigDecimal.valueOf(number);
        decimal = decimal.setScale(digits, RoundingMode.HALF_UP);
        return decimal.stripTrailingZeros().toPlainString();
    }

    // Format number to percentage without decimal
    public static String formatPercent(double number) {
        return formatPercent(number, 0);
    }

    // Format number to percentage with decimal
    public static String formatPercent(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(digits);
        return nf.format(number);
    }

    // Format bytes into a rounded string using IEC standard (matches Mac/Linux).
    // default up to 1 decimal place.
    public static String formatBytes(long bytes) {
        return formatBytes(bytes, 1);
    }

    // Format bytes into a rounded string using IEC standard (matches Mac/Linux).
    public static String formatBytes(long bytes, int digits) {
        return formatBytes(bytes, digits, false);
    }

    // Format bytes into a rounded string using decimal SI units.
    // default up to 1 decimal place.
    public static String formatSiBytes(long bytes) {
        return formatSiBytes(bytes, 1);
    }

    // Format bytes into a rounded string using decimal SI units.
    // Typically used to represent disk capacity, file size
    public static String formatSiBytes(long bytes, int digits) {
        return formatBytes(bytes, digits, true);
    }

    // Format bytes into human-readable string
    private static String formatBytes(long bytes, int digits, boolean siKilo) {
        String unit = siKilo ? "" : "i";
        final long K = siKilo ? 1000 : 1024;
        final long M = K * K;
        final long G = M * K;
        final long T = G * K;

        NumberFormat df = NumberFormat.getNumberInstance();
        df.setMaximumFractionDigits(digits);

        if (bytes >= T) return df.format(1.0 * bytes / T) + " T" + unit + "B";
        if (bytes >= G) return df.format(1.0 * bytes / G) + " G" + unit + "B";
        if (bytes >= M) return df.format(1.0 * bytes / M) + " M" + unit + "B";
        if (bytes >= K) return df.format(1.0 * bytes / K) + " K" + unit + "B";
        if (bytes > 0) return df.format(bytes) + " B";
        return "0";
    }

}