package com.arraywork.summer.security;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Security Model Attribute for Template
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/28
 */
@ControllerAdvice
public class SecurityAttribute {

    @Resource
    private SecuritySession session;

    // example: <div th:if="${principal}" th:text="${principal.username}"></div>

    /**
     * Global public attribute
     * <pre>
     *   <div th:if="${principal}" th:text="${principal.username}"></div>
     * </pre>
     */
    @ModelAttribute("principal")
    public Principal principal() {
        return session.getPrincipal();
    }

}