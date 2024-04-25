package com.arraywork.springfield.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.core.io.InputStreamSource;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Common Utilities
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
public class CommonUtils {

    // Thread sleep
    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Copy array range
    public static String[] copyOfRange(String[] source, int from, int to) {
        return Arrays.copyOfRange(source, from, to);
    }

    // Convert long to LocalDateTime
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    // Walk directory and add files to argument List<File>
    public static void walkDir(File dir, List<File> files) {
        File[] entries = dir.listFiles();
        for (File entry : entries) {
            if (entry.isFile()) {
                files.add(entry);
            } else if (entry.isDirectory()) {
                walkDir(entry, files);
            }
        }
    }

    // Manually trigger validation
    public static <T> List<String> validate(T entity, String[] properties) {
        for (String property : properties) {
            List<String> errors = validate(entity, property);
            if (errors != null) return errors;
        }
        return null;
    }

    // Manually trigger validation
    public static <T> List<String> validate(T entity, String property) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validator.validateProperty(entity, property);
        List<String> messages = errors.stream().map(e -> e.getMessage()).collect(Collectors.toList());
        return messages.isEmpty() ? null : messages;
    }

    // Determine whether the input source is in image format
    public static boolean isImageFormat(InputStreamSource source) throws IOException {
        byte[] header = new byte[4];
        InputStream inputStream = source.getInputStream();
        int read = inputStream.read(header);
        inputStream.close();

        if (read != -1) {
            String fileCode = toHexString(header).toUpperCase();
            return fileCode.matches("(FFD8FF|89504E|47494638|52494646).+"); // jpg|png|gif|webp
        }
        return false;
    }

    // Convert byte array to hexadecimal string
    public static String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) builder.append(0);
            builder.append(hex);
        }
        return builder.toString();
    }

    // Convert map to java entity
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> type) {
        try {
            T instance = type.getConstructor().newInstance();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value != null) {
                    try {
                        Field field = type.getDeclaredField(StringUtils.uncapitalize(key));
                        field.setAccessible(true);
                        field.set(instance, value);
                        field.setAccessible(false);
                    } catch (Exception e) {}
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成带省略号（以0表示）的页码数组
     * @param totalPages 总页数
     * @param pageNumber 当前页码
     * @param aroundNumber 环绕当前页左右的页码数
     * @return
     */
    public static List<Integer> calcEllipsisPages(int totalPages, int pageNumber, int aroundNumber) {
        int baseCount = aroundNumber * 2 + 5; // 总元素个数：环绕左右页码*2+当前页+省略号*2+首页+末页
        int surplus = baseCount - 2; // 只出现一个省略号时剩余元素个数
        int startIndex = 1 + 2 + aroundNumber + 1; // 前面出现省略号的临界点
        int endIndex = totalPages - 2 - aroundNumber - 1; // 后面出现省略号的临界点

        if (totalPages <= baseCount) { // 全部显示，不出现省略号
            return IntStream.rangeClosed(1, totalPages).boxed().toList();
        }
        if (pageNumber < startIndex) { // 只有后面出现省略号
            List<Integer> pages = IntStream.rangeClosed(1, surplus).boxed().collect(Collectors.toList());
            pages.add(0);
            pages.add(totalPages);
            return pages;
        }
        if (pageNumber > endIndex) { // 只有前边出现省略号
            int start = totalPages - surplus + 1;
            int end = start + surplus - 1;
            List<Integer> pages = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            pages.add(0, 0);
            pages.add(0, 1);
            return pages;
        }
        // 两边都有省略号
        int start = pageNumber - aroundNumber;
        int end = start + aroundNumber * 2;
        List<Integer> pages = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        pages.add(0, 0);
        pages.add(0, 1);
        pages.add(0);
        pages.add(totalPages);
        return pages;
    }

    public static List<Integer> calcEllipsisPages(int totalPages, int pageNumber) {
        return calcEllipsisPages(totalPages, pageNumber, 2);
    }

}