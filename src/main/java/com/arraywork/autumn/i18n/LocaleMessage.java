package com.arraywork.autumn.i18n;

import jakarta.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Locale Message Shortcuts
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
@Component
public class LocaleMessage {

    @Resource
    private MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String get(String code, Object arg) {
        return messageSource.getMessage(code, new Object[] { arg }, LocaleContextHolder.getLocale());
    }

    public String get(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}