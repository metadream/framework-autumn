package com.arraywork.autumn.error;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Unified Error Handler
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
@ControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Resource
    private HttpServletRequest request;

    // When dragging the progress bar of the output video stream, it is easy to
    // cause these exception and fall into an infinite loop, eventually causing the
    // system to crash. Ignoring them has not been found to have any impact.
    @ExceptionHandler({ ClientAbortException.class, AsyncRequestNotUsableException.class })
    public void handleClientAbort(ClientAbortException e) {
        // Ignore the ClientAbortException warning
        logger.warn("Ignored: {} - {}", request.getRequestURI(), e.getMessage());
    }

    // Custom exception (respond with custom status)
    @ExceptionHandler(HttpException.class)
    public String handle(HttpException e) {
        return forwardError(e.getStatus(), e);
    }

    // 400: Validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return forwardError(HttpStatus.BAD_REQUEST, message);
    }

    // 400
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        HttpMessageNotWritableException.class,
        MethodArgumentTypeMismatchException.class,
        IllegalStateException.class,
        IllegalArgumentException.class
    })
    public String handleBadRequest(Exception e) {
        return forwardError(HttpStatus.BAD_REQUEST, e);
    }

    // 401
    @ExceptionHandler(SecurityException.class)
    public String handle(SecurityException e) {
        return forwardError(HttpStatus.UNAUTHORIZED, e);
    }

    // 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        NoResourceFoundException.class,
        EntityNotFoundException.class
    })
    public String handleNotFoundException(Exception e) {
        return forwardError(HttpStatus.NOT_FOUND, "Resource not found");
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handle(HttpRequestMethodNotSupportedException e) {
        return forwardError(HttpStatus.METHOD_NOT_ALLOWED, e);
    }

    // 415
    @ExceptionHandler({
        HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class
    })
    public String handle(HttpMediaTypeNotSupportedException e) {
        return forwardError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e);
    }

    // Uncaught exceptions
    @ExceptionHandler(Exception.class)
    public String handle(Exception e) {
        logger.error("{}: {}", request.getRequestURI(), e.getMessage(), e);
        return forwardError(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    // Forward to error route
    private String forwardError(HttpStatus status, Exception e) {
        return forwardError(status, e.getMessage());
    }

    // Forward to error route
    private String forwardError(HttpStatus status, String message) {
        logger.error("{}: {}", request.getRequestURI(), message);
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status.value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
        return "forward:/error";
    }

}