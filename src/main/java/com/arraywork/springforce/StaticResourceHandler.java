package com.arraywork.springforce;

import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Static Resource Request Handler
 * WARNING: NOT SUITABLE FOR PLAYING VIDEO RESOURCES
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
@Component
public class StaticResourceHandler extends ResourceHttpRequestHandler {

    public final static String ATTR_FILE = "NON_STATIC_FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        return new FileSystemResource((Path) request.getAttribute(ATTR_FILE));
    }

}