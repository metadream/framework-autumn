package com.arraywork.springforce;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Static Resource Handler
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/29
 */
@Component
public class StaticResourceHandler {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    // Serve static resources reqeust
    public void serve(Path path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Resource resource = new FileSystemResource(path);

        // If resource not exists, return 404
        if (!resource.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // If resource not modified, return 304
        long lastModified = resource.lastModified();
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (checkNotModified(lastModified, ifModifiedSince)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // Check range http header
        long total = resource.contentLength();
        long start = 0, end = total;
        String range = request.getHeader(HttpHeaders.RANGE);

        if (range != null) {
            // If cannot parse range, return 416
            Matcher matcher = Pattern.compile("bytes=(\\d+)\\-(\\d+)?").matcher(range);
            if (!matcher.find()) {
                response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + total);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            start = Long.parseLong(matcher.group(1));
            end = matcher.group(2) != null ? Long.parseLong(matcher.group(2)) : total - 1;
            end = Math.min(end, total - 1);

            // Set range response headers
            response.setContentLengthLong(end - start + 1);
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + '-' + end + '/' + total);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }

        // Set generic response headers
        String mimeType = request.getServletContext().getMimeType(resource.getFilename());
        response.setContentType(mimeType);
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
        response.setBufferSize(DEFAULT_BUFFER_SIZE);

        // Streaming resource bytes
        try (InputStream input = resource.getInputStream();
            OutputStream output = response.getOutputStream()) {
            copy(input, output, start);
            input.close();
            output.close();
        }
    }

    // Copy input stream to output stream by start
    public long copy(InputStream input, OutputStream output, long start) throws IOException {
        input.skip(start);
        long copied = 0;
        int read = -1;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
            copied += read;
        }
        output.flush();
        return copied;
    }

    // Copy input stream to output stream
    public long copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, 0);
    }

    // Close stream quietly
    public void close(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException e) {
            // ignore
        }
    }

    // Check last modified or not
    private boolean checkNotModified(long lastModified, long ifModifiedSince) {
        return lastModified > 0 && ifModifiedSince > 0 && ifModifiedSince >= lastModified / 1000 * 1000;
    }

}