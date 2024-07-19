package com.arraywork.springforce.databind;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Generic Enumeration
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
public interface GenericEnum<T> {

    /**
     * Use the corresponding value of @JsonValue when passing parameters and
     * returning, overriding the original default implementation of deserialization
     * from literals back to enumerations
     */
    @JsonValue
    T getCode();

}