package com.arraywork.deps.util;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import com.arraywork.deps.error.HttpException;

/**
 * Assertion
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
public class Assert extends org.springframework.util.Assert {

    /**
     * 断言对象不是null
     * @param object
     * @param error
     */
    public static void notNull(@Nullable Object object, HttpStatus status) {
        if (object == null) {
            throw new HttpException(status);
        }
    }

    /**
     * 断言表达式为真
     * @param expression
     * @param error
     */
    public static void isTrue(boolean expression, HttpStatus status) {
        if (!expression) {
            throw new HttpException(status);
        }
    }

    /**
     * 断言字符串有内容（去掉空格后仍然有内容）
     * @param text
     * @param error
     */
    public static void hasText(@Nullable String text, HttpStatus status) {
        if (!StringUtils.hasText(text)) {
            throw new HttpException(status);
        }
    }

    /**
     * 断言字符串有长度（空格被认为是有长度）
     * @param text
     * @param message
     */
    public static void hasLength(@Nullable String text, HttpStatus status) {
        if (!StringUtils.hasLength(text)) {
            throw new HttpException(status);
        }
    }

}