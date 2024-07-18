package com.arraywork.springforce.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation: Authority
 * example @Authority({"ADMIN", "USER"})
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {

    String[] value() default {};

}