package com.arraywork.springhood.error;

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

    private static final long serialVersionUID = -1835312615458372183L;
    private HttpStatus status;

    public HttpException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}