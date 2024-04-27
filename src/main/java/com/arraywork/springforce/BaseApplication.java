package com.arraywork.springforce;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * SringBoot Base Application
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.springforce")
@ServletComponentScan("com.arraywork.springforce.security") // @WebListener support
public class BaseApplication {

    // 覆盖默认的序列化和反序列化对象
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 在反序列化时按照字母顺序对属性排序
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // 在反序列化时忽略 JSON 中存在但 Java 对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 在反序列化时将空字符串转换为NULL对象
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
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
        return mapper;
    }

}