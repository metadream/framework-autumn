package com.arraywork.autumn.util;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Jackson Serialization and Deserialization
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    /** Overrides the default serialization and deserialization module */
    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(df));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(df));

        mapper.registerModule(javaTimeModule);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /** Serialize Java object to JSON string */
    public static <T> String stringify(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON string to generic Java type */
    public static <T> T parse(String json, Class<T> type) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON string to generic Java type */
    public static <T> T parse(String json, TypeReference<T> typeReference) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON string to list with generic Java type */
    public static <T> T parseList(String json, Class<T> type) {
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return json == null || json.isEmpty() ? null : mapper.readValue(json, listType);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON file to generic Java type */
    public static <T> T parse(File file, Class<T> type) {
        try {
            return file == null ? null : mapper.readValue(file, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON string to JsonNode type */
    public static JsonNode parse(String json) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserialize JSON file to JsonNode type */
    public static JsonNode parse(File file) {
        try {
            return mapper.readTree(file);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Convert an object to Map */
    public static Map<String, Object> convertToMap(Object object) {
        return mapper.convertValue(object, new TypeReference<>() { });
    }

    /** Convert an object to Entity */
    public static <T> T convertToEntity(Object object, Class<T> type) {
        return mapper.convertValue(object, type);
    }

}