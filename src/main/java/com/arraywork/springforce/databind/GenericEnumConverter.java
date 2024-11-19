package com.arraywork.springforce.databind;

import java.lang.reflect.ParameterizedType;
import jakarta.persistence.AttributeConverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Generic Enumeration Converter
 * CustomEnum: public static class Converter extends GenericEnumConverter<CustomEnum, Integer> {}
 * Entity: @Convert(converter = CustomEnum.Converter.class)
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
public class GenericEnumConverter<X extends GenericEnum<Y>, Y>
    implements AttributeConverter<X, Y>, ConverterFactory<String, GenericEnum<?>> {

    // Implement the ConverterFactory method to convert the code in the 'GET'
    // request into an enumeration type
    @Override
    public <T extends GenericEnum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    // Implement the JPA AttributeConverter method to convert generic enumeration
    // into database column value
    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return (attribute != null) ? attribute.getCode() : null;
    }

    // Implement the JPA AttributeConverter method to convert database column value
    // into generic enumeration
    @Override
    public X convertToEntityAttribute(Y code) {
        @SuppressWarnings("unchecked") // Get the specific type of the enumeration
        Class<X> clazz = (Class<X>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        for (X x : clazz.getEnumConstants()) {
            if (x.getCode().equals(code)) return x;
        }
        return null;
    }

    // Convert the object (usually basic types such as integer and string) to
    // generic enumeration
    private record StringToEnumConverter<T extends GenericEnum<?>>(
        Class<T> targetType) implements Converter<String, T> {

        @Override
        public T convert(String code) {
            T[] enums = targetType.getEnumConstants();
            for (T e : enums) {
                if (code.equals(String.valueOf(e.getCode()))) return e;
            }
            return null;
        }

    }

}