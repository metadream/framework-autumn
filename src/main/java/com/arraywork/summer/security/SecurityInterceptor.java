package com.arraywork.summer.security;

import java.lang.reflect.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.arraywork.summer.util.Assert;

/**
 * Security Interceptor
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    @Resource
    private SecuritySession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod handlerMethod) {
            // Get @Permission annotation in controller method
            Method method = handlerMethod.getMethod();
            Permission permission = method.getAnnotation(Permission.class);

            // Annotation exists?
            if (permission != null) {
                // Is logged in?
                Principal principal = session.getPrincipal();
                Assert.notNull(principal, HttpStatus.UNAUTHORIZED);

                // Has any role?
                String[] roles = permission.value();
                boolean hasPermission = roles == null || roles.length == 0 || principal.hasSecurityRoles(roles);
                Assert.isTrue(hasPermission, HttpStatus.FORBIDDEN);
            }
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/**");
    }

}