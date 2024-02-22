package com.arraywork.dep.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.InputStreamSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Common Utilities
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

}