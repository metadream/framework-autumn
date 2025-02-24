package com.arraywork.summer.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;

/**
 * Validator Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
public class Validator {

    private static jakarta.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Manually trigger validation for entity or its fields */
    public static <T> void validate(T entity, String... fields) {
        Set<ConstraintViolation<T>> errors = new LinkedHashSet<>();
        if (fields == null || fields.length == 0) {
            errors.addAll(validator.validate(entity));
        } else {
            for (String field : fields) {
                errors.addAll(validator.validateProperty(entity, field));
            }
        }
        if (!errors.isEmpty()) {
            String messages = errors.stream().map(e -> e.getMessage()).collect(Collectors.joining(","));
            throw new ConstraintViolationException(messages, errors);
        }
    }

    /**
     * Validation Group Interface for Entity Field
     * (with Spring's @Validated grouping verification)
     */
    public interface Insert { }

    public interface Update { }

    public interface Delete { }

}