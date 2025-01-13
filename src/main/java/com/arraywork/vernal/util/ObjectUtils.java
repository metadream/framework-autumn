package com.arraywork.vernal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * Object Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
public class ObjectUtils {

    /** Get field values by field name reflection (setter/getter entities only) */
    public static Object invokeGetter(Object instance, String fieldName) throws Exception {
        Class<?> clazz = instance.getClass();
        fieldName = StringUtils.capitalize(fieldName);
        Method getter = clazz.getDeclaredMethod("get" + fieldName);
        return getter.invoke(instance);
    }

    /** Convert map to specified java type */
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
                        // ignores
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