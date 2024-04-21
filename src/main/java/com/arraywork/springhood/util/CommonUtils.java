package com.arraywork.springhood.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Copy array range
    public static String[] copyOfRange(String[] source, int from, int to) {
        return Arrays.copyOfRange(source, from, to);
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

}