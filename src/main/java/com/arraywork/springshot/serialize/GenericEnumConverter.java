package com.arraywork.springshot.serialize;

import java.lang.reflect.ParameterizedType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenericEnumConverter<T extends GenericEnum<E>, E>
    implements AttributeConverter<T, E> {

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return (attribute != null) ? attribute.getValue() : null;
    }

    @Override
    public T convertToEntityAttribute(E value) {
        Class<T> clazz = getEnumClass();
        for (T e : clazz.getEnumConstants()) {
            if (e.getValue().equals(value)) return e;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEnumClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}