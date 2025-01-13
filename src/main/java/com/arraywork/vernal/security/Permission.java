package com.arraywork.vernal.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation: Permission
 * <pre>
 *   @Permission({"ADMIN", "USER"})
 * </pre>
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String[] value() default {};
}