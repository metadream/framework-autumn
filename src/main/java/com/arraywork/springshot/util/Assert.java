package com.arraywork.springshot.util;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import com.arraywork.springshot.error.HttpException;

/**
 * Assertion Custom Extension
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
public class Assert extends org.springframework.util.Assert {

    // Not null?
    public static void notNull(@Nullable Object object, HttpStatus status) {
        if (object == null) throw new HttpException(status);
    }

    // Is true?
    public static void isTrue(boolean expression, HttpStatus status) {
        if (!expression) throw new HttpException(status);
    }

    // Has length?
    public static void hasLength(@Nullable String text, HttpStatus status) {
        if (!StringUtils.hasLength(text)) throw new HttpException(status);
    }

    // Has text?
    public static void hasText(@Nullable String text, HttpStatus status) {
        if (!StringUtils.hasText(text)) throw new HttpException(status);
    }

}