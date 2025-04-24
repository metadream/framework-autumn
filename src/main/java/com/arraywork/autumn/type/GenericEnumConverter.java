package com.arraywork.autumn.type;

import java.lang.reflect.ParameterizedType;
import jakarta.persistence.AttributeConverter;

import org.springframework.context.annotation.Configuration;

/**
 * Generic Enumeration Converter
 * It is used for converting between entity attributes and database column values.
 *
 * CustomEnum: public static class Converter extends GenericEnumConverter<CustomEnum, Integer> {}
 * Entity: @Convert(converter = CustomEnum.Converter.class)
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
@Configuration
public class GenericEnumConverter<E extends GenericEnum<T>, T>
    implements AttributeConverter<E, T> {

    /** E (enum object) -> T (database column value) */
    @Override
    public T convertToDatabaseColumn(E attribute) {
        return (attribute != null) ? attribute.getCode() : null;
    }

    /** T (database column value) -> E (enum object) */
    @Override
    public E convertToEntityAttribute(T source) {
        Class<E> clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return GenericEnum.codeOf(source, clazz);
    }

}