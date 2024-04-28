package com.arraywork.springforce.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * Object Utilities
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
public class Objects {

    // Convert map to specified java type
    public static <T> T convertMap(Map<String, Object> map, Class<T> type) {
        try {
            T instance = type.getConstructor().newInstance();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value != null) {
                    try {
                        Field field = type.getDeclaredField(StringUtils.uncapitalize(key));
                        field.setAccessible(true);
                        field.set(instance, value);
                        field.setAccessible(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}