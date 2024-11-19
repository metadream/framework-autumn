package com.arraywork.springforce.databind;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Generic Enumeration
 *
 * @author AiChen
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
    T getCode();

    /**
     * 反序列化
     * 一般情况下，例如前端传入的枚举 code，Spring 通过 GenericEnumConverter.Converter 方法反序列化，
     * 该方法根据 code 反查枚举对象；
     * 某些情况下，例如经过 @JsonFormat(shape = JsonFormat.Shape.OBJECT) 全属性序列化之后再次反序列化时，
     * 传入的是全属性的 JSON 对象，需从中提取 code 再反查。
     *
     * @param object
     * @param enumClass
     * @param <E>
     * @return
     */
    static <E extends GenericEnum> E fromCode(Object object, Class<E> enumClass) {
        String code = null;
        if (object instanceof String _code) {
            code = _code;
        } else if (object instanceof Integer _code) {
            code = String.valueOf(_code);
        } else if (object instanceof LinkedHashMap map) {
            code = String.valueOf(map.get("code"));
        }

        for (E e : enumClass.getEnumConstants()) {
            if (String.valueOf(e.getCode()).equals(code)) return e;
        }
        return null;
    }

}