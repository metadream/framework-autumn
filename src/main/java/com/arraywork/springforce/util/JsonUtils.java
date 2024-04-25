package com.arraywork.springforce.util;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * 工具类：Jackson 序列化与反序列化
 * @author AiChen
 * @created 2020/03/01
 */
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        ObjectMapper mapper = new ObjectMapper();

        // 在反序列化时按照字母顺序对属性排序
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // 在反序列化时忽略 JSON 中存在但 Java 对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 在序列化时忽略空的 Java 对象
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 在序列化时忽略值为 null 的属性
        mapper.setSerializationInclusion(Include.NON_NULL);

        // 支持 LocalDate/Time 对象序列化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(df));
        mapper.registerModule(javaTimeModule);
    }

    /**
     * 将Java对象转换为JSON字符串
     * @param <T>
     * @param object
     * @return
     */
    public static <T> String stringify(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON字符串解析为泛型Java对象
     * @param <T>
     * @param json
     * @param type
     * @return
     */
    public static <T> T parse(String json, Class<T> type) {
        try {
            return json == null || json.length() == 0 ? null : mapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON字符串解析为泛型Java对象
     * @param <T>
     * @param json
     * @param typeReference
     * @return
     */
    public static <T> T parse(String json, TypeReference<T> typeReference) {
        try {
            return json == null || json.length() == 0 ? null : mapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON字符串解析为List泛型Java对象
     * @param <T>
     * @param json
     * @param type
     * @return
     */
    public static <T> T parseList(String json, Class<T> type) {
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return json == null || json.length() == 0 ? null : mapper.readValue(json, listType);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON文件解析为泛型Java对象
     * @param <T>
     * @param file
     * @param type
     * @return
     */
    public static <T> T parse(File file, Class<T> type) {
        try {
            return file == null ? null : mapper.readValue(file, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON字符串解析为JsonNode对象
     * @param json
     * @return
     */
    public static JsonNode parse(String json) {
        try {
            return json == null || json.length() == 0 ? null : mapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON文件解析为JsonNode对象
     * @param file
     * @return
     */
    public static JsonNode parse(File file) {
        try {
            return mapper.readTree(file);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将对象转换为Map
     * @param obj
     * @return
     */
    public static Map<String, Object> convertToMap(Object obj) {
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

}