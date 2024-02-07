package com.arraywork.deps.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Protected Annotation
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Protected {}