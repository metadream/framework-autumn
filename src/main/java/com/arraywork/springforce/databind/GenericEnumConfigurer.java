package com.arraywork.springforce.databind;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Generic Enumeration Configuration
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/05/03
 */
@Configuration
public class GenericEnumConfigurer implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new GenericEnumConverter<>());
    }

}