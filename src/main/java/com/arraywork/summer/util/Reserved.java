package com.arraywork.summer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reserved (classes, methods, etc. that may be used in the future)
 * No impact on the code, just for query
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/11/13
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Reserved { }