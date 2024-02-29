package com.arraywork.springshot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.arraywork.springshot.external.BCryptEncoder;

/**
 * Security Config
 * Inject some global attributes and beans
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/28
 */
@ControllerAdvice
public class SecurityConfig {

    @Autowired
    private SecurityContext context;

    // @example: <div th:if="${principal}" th:text="${principal.username}"></div>
    @ModelAttribute("principal")
    public Principal principal() {
        return context.getPrincipal();
    }

    @Bean
    public BCryptEncoder bCryptEncoder() {
        return new BCryptEncoder();
    }

}