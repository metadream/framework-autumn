package com.arraywork.autumn.i18n;

import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Locale Configuration
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
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
        CookieLocaleResolver resolver = new CookieLocaleResolver();
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
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceMessageFormatter();
        source.setBasenames("i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        return source;
    }

    /**
     * Resource Message Formatter
     *
     * @author Marco
     * @copyright ArrayWork Inc.
     * @since 2024/03/02
     */
    class ResourceMessageFormatter extends ResourceBundleMessageSource {
        /** Escape single quotes in the message */
        @Override
        protected String getStringOrNull(ResourceBundle bundle, String key) {
            if (bundle.containsKey(key)) {
                String message = bundle.getString(key);
                return message.replaceAll("'", "''");
            }
            return null;
        }
    }

}