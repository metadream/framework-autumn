package com.arraywork.dep.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

/**
 * Global Error Handler
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    // Custom exception (respond with custom status)
    @ExceptionHandler(HttpException.class)
    public ErrorResponse handle(HttpException e) {
        return buildErrorResponse(e.getStatus(), e);
    }

    // 400: Validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 400
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        HttpMessageNotWritableException.class,
        MethodArgumentTypeMismatchException.class,
        IllegalStateException.class,
        IllegalArgumentException.class
    })
    public ErrorResponse handleBadRequest(Exception e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    // 401
    @ExceptionHandler(SecurityException.class)
    public ErrorResponse handle(SecurityException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e);
    }

    // 404
    @ExceptionHandler(NoResourceFoundException.class)
    public ErrorResponse handle(NoResourceFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, e);
    }

    // 415
    @ExceptionHandler({
        HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class
    })
    public ErrorResponse handle(HttpMediaTypeNotSupportedException e) {
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e);
    }

    // Uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ErrorResponse handle(Exception e) {
        String message = e.getMessage();
        String path = request.getRequestURI();
        logger.error("{}: {}", message, path, e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        response.setStatus(status.value());
        return new ErrorResponse(status.value(), status.getReasonPhrase(), path);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        String path = request.getRequestURI();
        logger.error("{}: {}", message, path);

        response.setStatus(status.value());
        return new ErrorResponse(status.value(), message, path);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, Exception e) {
        return buildErrorResponse(status, e.getMessage());
    }

    @Data
    class ErrorResponse {

        private long timestamp;
        private int status;
        private String message;
        private String path;

        public ErrorResponse(int status, String message, String path) {
            this.timestamp = System.currentTimeMillis();
            this.status = status;
            this.message = message.replaceAll(":.+", "");
            this.path = path;
        }

    }

}