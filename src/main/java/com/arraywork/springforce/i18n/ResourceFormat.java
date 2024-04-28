package com.arraywork.springforce.i18n;

import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Custom Resource Format
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/03/02
 */
public class ResourceFormat extends ResourceBundleMessageSource {

    // Escape single quotes in the message
    @Override
    protected String getStringOrNull(ResourceBundle bundle, String key) {
        if (bundle.containsKey(key)) {
            String message = bundle.getString(key);
            return message.replaceAll("'", "''");
        }
        return null;
    }

}