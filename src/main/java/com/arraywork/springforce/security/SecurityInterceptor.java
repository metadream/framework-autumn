package com.arraywork.springforce.security;

import java.io.IOException;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.arraywork.springforce.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security Interceptor
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    @Autowired
    private SecurityContext context;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws IOException {

        if (handler instanceof HandlerMethod) {
            // Get @Authority annotation in controller method
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Authority authority = method.getAnnotation(Authority.class);

            // Annotation exists?
            if (authority != null) {
                // Is logged in?
                Principal principal = context.getPrincipal();
                Assert.notNull(principal, HttpStatus.UNAUTHORIZED);

                // Has any role?
                String[] roles = authority.value();
                boolean hasAuthority = roles == null || roles.length == 0 || principal.hasAnyRole(roles);
                Assert.isTrue(hasAuthority, HttpStatus.FORBIDDEN);
            }
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/**");
    }

}