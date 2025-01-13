package com.arraywork.vernal.type;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Generic Enumeration
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/26
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)  // 正序列化：将枚举序列化为完整的JSON对象形式
public interface GenericEnum<T> {

    /**
     * Use the corresponding value of @JsonValue when passing parameters and
     * returning, overriding the original default implementation of deserialization
     * from literals back to enumerations
     */
    T code();

    /** Deserialization method */
    static <E extends GenericEnum> E codeOf(Object object, Class<E> enumClass) {
        String code = null;
        if (object instanceof String _code) {
            code = _code;
        } else if (object instanceof Integer _code) {
            code = String.valueOf(_code);
        } else if (object instanceof LinkedHashMap map) { // 全属性反序列化
            code = String.valueOf(map.get("code"));
        }

        for (E e : enumClass.getEnumConstants()) {
            if (String.valueOf(e.code()).equals(code)) return e;
        }
        return null;
    }

}