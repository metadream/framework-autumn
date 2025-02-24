package com.arraywork.summer.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Number Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/03/11
 */
public class NumberUtils {

    private static final Random random = ThreadLocalRandom.current();

    /** Rounding number without decimal */
    public static String formatDecimal(double number) {
        return formatDecimal(number, 0);
    }

    /** Rounding number with decimal digits */
    public static String formatDecimal(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        BigDecimal decimal = BigDecimal.valueOf(number);
        decimal = decimal.setScale(digits, RoundingMode.HALF_UP);
        return decimal.stripTrailingZeros().toPlainString();
    }

    /** Format number to percentage without decimal */
    public static String formatPercent(double number) {
        return formatPercent(number, 0);
    }

    /** Format number to percentage with decimal */
    public static String formatPercent(double number, int digits) {
        if (Double.isNaN(number)) number = 0;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(digits);
        return nf.format(number);
    }

    /**
     * Format bytes into a rounded string using IEC standard (matches Mac/Linux).
     * default up to 1 decimal place.
     */
    public static String formatBytes(long bytes) {
        return formatBytes(bytes, 1);
    }

    /**
     * Format bytes into a rounded string using IEC standard (matches Mac/Linux).
     */
    public static String formatBytes(long bytes, int digits) {
        return formatBytes(bytes, digits, false);
    }

    /**
     * Format bytes into a rounded string using decimal SI units.
     * default up to 1 decimal place.
     */
    public static String formatSiBytes(long bytes) {
        return formatSiBytes(bytes, 1);
    }

    /**
     * Format bytes into a rounded string using decimal SI units.
     * Typically used to represent disk capacity, file size
     */
    public static String formatSiBytes(long bytes, int digits) {
        return formatBytes(bytes, digits, true);
    }

    /** Format bytes into human-readable string */
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

    /**
     * Generate a random number in a given interval
     *
     * @param origin The least value that can be returned
     * @param bound  The upper bound (exclusive) for the returned value
     * @return
     */
    public static int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    /**
     * Generate a set of unique random numbers in a given interval
     *
     * @param origin The least value that can be returned
     * @param bound  The upper bound (exclusive) for the returned value
     * @param size   Number of random result (positive integer and cannot exceed the given interval)
     */
    public static Set<Integer> nextInt(int origin, int bound, int size) {
        org.springframework.util.Assert.isTrue(size > 0 && size <= bound - origin,
            "The size of random numbers must be a positive integer and cannot exceed the given interval");
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < size) {
            Integer next = random.nextInt(origin, bound);
            numbers.add(next);
        }
        return numbers;
    }

    /**
     * Determine whether the value is within the interval
     *
     * @param value     The value to be determined
     * @param intervals Mathematical interval
     * @return
     */
    public static boolean inIntervals(BigDecimal value, String intervals) {
        Pattern pattern = Pattern.compile("^\\s*(?<leftSymbol>(\\(|\\[))\\s*(?<minValue>[-+]?\\d+(\\.\\d+)?)\\s*,\\s*(?<maxValue>[-+]?\\d+(\\.\\d+)?)\\s*(?<rightSymbol>(\\)|\\]))");
        Matcher matcher = pattern.matcher(intervals);
        Assert.isTrue(matcher.find(), "Not a valid interval definition: " + intervals);

        BigDecimal minValue = new BigDecimal(matcher.group("minValue"));
        BigDecimal maxValue = new BigDecimal(matcher.group("maxValue"));
        Assert.isTrue(minValue.compareTo(maxValue) <= 0, "The left value of the interval cannot be greater than the right value: " + (minValue + " > " + maxValue));

        String leftSymbol = matcher.group("leftSymbol");
        String rightSymbol = matcher.group("rightSymbol");
        // Determine the opening and closing interval
        return (!"(".equals(leftSymbol) || value.compareTo(minValue) > 0) && (!")".equals(rightSymbol) || value.compareTo(maxValue) < 0)
            && (!"[".equals(leftSymbol) || value.compareTo(minValue) >= 0) && (!"]".equals(rightSymbol) || value.compareTo(maxValue) <= 0);
    }

}