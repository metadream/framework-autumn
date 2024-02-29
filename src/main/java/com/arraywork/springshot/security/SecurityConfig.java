package com.arraywork.springshot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.arraywork.springshot.lib.BCryptEncoder;

/**
 * Security Config
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/28
 */
@ControllerAdvice
public class SecurityConfig {

    @Autowired
    private SecurityContext context;

    @ModelAttribute("principal")
    public Principal principal() {
        return context.getPrincipal();
    }

    @Bean
    public BCryptEncoder bCryptEncoder() {
        return new BCryptEncoder();
    }

}