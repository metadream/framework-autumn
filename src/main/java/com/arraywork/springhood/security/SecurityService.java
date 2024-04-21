package com.arraywork.springhood.security;

/**
 * Security Service
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/28
 */
public interface SecurityService {

    Principal login(String username, String rawPassword);

}