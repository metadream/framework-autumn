package com.arraywork.springforce.security;

/**
 * Security Service
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/28
 */
public interface SecurityService {

    Principal login(String username, String rawPassword);

}