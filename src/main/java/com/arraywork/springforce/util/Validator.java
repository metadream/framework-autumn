package com.arraywork.springforce.util;

import java.util.List;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;

/**
 * Validator Utilities
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
public class Validator {

    // Manually trigger validation for multiple properties
    public static <T> List<String> validate(T entity, String[] properties) {
        for (String property : properties) {
            List<String> errors = validate(entity, property);
            if (errors != null) return errors;
        }
        return null;
    }

    // Manually trigger validation for single property
    public static <T> List<String> validate(T entity, String property) {
        jakarta.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validator.validateProperty(entity, property);
        List<String> messages = Arrays.map(errors, e -> e.getMessage());
        return messages.isEmpty() ? null : messages;
    }

    /**
     * Validation Group Interface for Entity Field
     * (with Spring's @Validated grouping verification)
     */
    public interface Insert {
    }

    public interface Update {
    }

    public interface Delete {
    }

}