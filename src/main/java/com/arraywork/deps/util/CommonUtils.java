package com.arraywork.deps.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public static <T> List<String> validate(T entity, String property) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validator.validateProperty(entity, property);
        return errors.stream().map(e -> e.getMessage()).collect(Collectors.toList());
    }

}