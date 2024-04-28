package com.arraywork.springforce.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Locale Message Shortcuts
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
@Component
public class LocaleMessage {

    @Autowired
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