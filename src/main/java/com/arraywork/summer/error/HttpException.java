package com.arraywork.summer.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

/**
 * Custom HTTP Exception
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@Getter
public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public HttpException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpException(HttpStatusCode statusCode, String message) {
        super(message);
        this.status = HttpStatus.valueOf(statusCode.value());
    }

}