package com.arraywork.springforce.error;

import java.io.Serial;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Custom HTTP Exception
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@Getter
public class HttpException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1835312615458372183L;
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

}