package com.arraywork.deps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.arraywork.deps.security.Authenticator;

/**
 * Web Boot Application
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.deps")
public class BootApplication implements WebMvcConfigurer {

    @Autowired
    private Authenticator authenticator;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticator).addPathPatterns("/**");
    }

}