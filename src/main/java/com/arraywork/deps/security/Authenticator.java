package com.arraywork.deps.security;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.arraywork.deps.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Authentication Interceptor
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/07
 */
@Component
public class Authenticator implements HandlerInterceptor {

    @Autowired
    private HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            Protected anno = method.getAnnotation(Protected.class);

            if (anno != null) {
                Object object = session.getAttribute(session.getId());
                Assert.notNull(object, HttpStatus.UNAUTHORIZED);
            }
        }
        return true;
    }

}