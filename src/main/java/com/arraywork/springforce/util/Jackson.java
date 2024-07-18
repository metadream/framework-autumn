package com.arraywork.springforce.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Jackson Serialization and Deserialization
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2020/03/01
 */
@Component
public class Jackson {

    @Resource
    private ObjectMapper mapper;

    // Serialize Java object to JSON string
    public <T> String stringify(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON string to generic Java type
    public <T> T parse(String json, Class<T> type) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON string to generic Java type
    public <T> T parse(String json, TypeReference<T> typeReference) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON string to list with generic Java type
    public <T> T parseList(String json, Class<T> type) {
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return json == null || json.isEmpty() ? null : mapper.readValue(json, listType);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON file to generic Java type
    public <T> T parse(File file, Class<T> type) {
        try {
            return file == null ? null : mapper.readValue(file, type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON string to JsonNode type
    public JsonNode parse(String json) {
        try {
            return json == null || json.isEmpty() ? null : mapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Deserialize JSON file to JsonNode type
    public JsonNode parse(File file) {
        try {
            return mapper.readTree(file);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Convert an object to a Map
    public Map<String, Object> convertToMap(Object object) {
        return mapper.convertValue(object, new TypeReference<>() {
        });
    }

}