package com.arraywork.springforce.databind;

import java.lang.reflect.ParameterizedType;

import jakarta.persistence.AttributeConverter;

/**
 * Generic Enumeration Converter
 *
 * CustomEnum: public static class Converter extends GenericEnumConverter<CustomEnum, Integer> {}
 * Entity: @Convert(converter = CustomEnum.Converter.class)
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
public class GenericEnumConverter<T extends GenericEnum<E>, E> implements AttributeConverter<T, E> {

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return (attribute != null) ? attribute.getCode() : null;
    }

    @Override
    public T convertToEntityAttribute(E code) {
        Class<T> clazz = getEnumClass();
        for (T e : clazz.getEnumConstants()) {
            if (e.getCode().equals(code)) return e;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEnumClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}