package com.arraywork.springforce.id;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

/**
 * Nano Id Generation Annotation
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/07
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(KeyGenerator.NanoId.class)
public @interface NanoIdGeneration {}