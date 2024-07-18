package com.arraywork.springforce.security;

import java.util.List;

import com.arraywork.springforce.util.Arrays;

import lombok.Data;

/**
 * Authenticated Principal
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Data
public abstract class Principal {

    private long id;
    private String username;
    private String password;

    public abstract List<SecurityRole> getSecurityRoles();

    public boolean hasRole(String roleName) {
        List<SecurityRole> roles = getSecurityRoles();
        return roles != null && Arrays.findAny(roles, v -> v.name().equals(roleName)) != null;
    }

    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) return true;
        }
        return false;
    }

}