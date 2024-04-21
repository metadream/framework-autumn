package com.arraywork.springhood.error;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.RequestDispatcher;

/**
 * Unified error message format
 * Override SpringBoot's default error attributes
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2020/03/01
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        // Get default error attributes and remove unnecessary fields
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, options);
        attributes.remove("error");
        attributes.remove("errors");
        attributes.remove("trace");

        // Get the message set in ErrorHandler@ControllerAdvice
        // and prefer custom error messages
        Object message = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);
        if (message != null) {
            attributes.put("message", message);
        }
        return attributes;
    }

}