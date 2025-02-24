package com.arraywork.autumn.security;

/**
 * Security Service
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/28
 */
public interface SecurityService {
    Principal login(String username, String rawPassword);
}