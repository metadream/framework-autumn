package com.arraywork.autumn.type;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Generic Enumeration Converter Factory
 * It's used for deserializing enums in @RequestParam and @PathVariable.
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/04/24
 */
@Configuration
public class GenericEnumConverterFactory
    implements ConverterFactory<String, GenericEnum>, WebMvcConfigurer {

    @Override
    public <E extends GenericEnum> Converter<String, E> getConverter(Class<E> targetType) {
        return source -> GenericEnum.codeOf(source, targetType);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(this);
    }

}