package com.arraywork.springforce;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.arraywork.springforce.external.BCryptEncoder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * SringBoot Base Application
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.springforce")
@ServletComponentScan("com.arraywork.springforce.security") // @WebListener support
public class BaseApplication {

    // Get the home directory of application
    // also where the JAR package located
    @Bean
    public String appHome() {
        return new ApplicationHome().getDir().toString();
    }

    // BCrypt strong hashing encoder
    @Bean
    public BCryptEncoder bCryptEncoder() {
        return new BCryptEncoder();
    }

    // Web socket endpoint
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    // Overrides the default serialization and deserialization module
    @Bean
    public ObjectMapper objectMapper() {
        // Support LocalDate/Time serialization
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(df));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(javaTimeModule);
        // Ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Convert empty string to a NULL object
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // Sort the attributes alphabetically
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // Ignore null beans
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // Ignore null properties
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }

}