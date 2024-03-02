package com.arraywork.springshot.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Locale Configuration
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/26
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(LocaleContextHolder.getLocale());
        return resolver;
    }

    /**
     * If you don't need to escape the message, add the following directly
     * to application.properties:
     * <pre>
     *   spring.messages.basename = i18n/messages
     *   spring.messages.fallbackToSystemLocale = false
     * <pre>
     * @return
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceFormat();
        source.setBasenames("i18n/messages");
        source.setFallbackToSystemLocale(false);
        return source;
    }

}