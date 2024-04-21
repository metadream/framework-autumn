package com.arraywork.springshot.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * Objects Utilities
 * @author AiChen
 */
public class Objects {

    // Convert map to java entity
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> type) {
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
                    } catch (Exception e) {}
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}