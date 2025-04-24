package com.arraywork.autumn.type;

import java.io.IOException;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Generic Enumeration Deserializer
 * It's used for deserializing enums in @RequestBody
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/04/19
 */
public class GenericEnumDeserializer extends JsonDeserializer<GenericEnum<?>> {

    @Override
    public GenericEnum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = jp.getText();
        if (StringUtils.hasText(value)) {
            Class<?> targetType = BeanUtils.findPropertyType(jp.currentName(), jp.currentValue().getClass());
            if (GenericEnum.class.isAssignableFrom(targetType)) {
                Class<? extends GenericEnum<?>> enumClass = (Class<? extends GenericEnum<?>>) targetType;
                return GenericEnum.codeOf(value, enumClass);
            }
        }
        return null;
    }

}