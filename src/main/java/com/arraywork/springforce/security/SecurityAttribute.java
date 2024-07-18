package com.arraywork.springforce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Security Model Attribute for Template
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/28
 */
@ControllerAdvice
public class SecurityAttribute {

    @Autowired
    private SecurityContext context;

    // example: <div th:if="${principal}" th:text="${principal.username}"></div>
    @ModelAttribute("principal")
    public Principal principal() {
        return context.getPrincipal();
    }

}