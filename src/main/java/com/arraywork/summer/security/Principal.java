package com.arraywork.summer.security;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * Authenticated Principal
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
@Data
public abstract class Principal {

    private String id;
    private String username;
    private String nickname;
    private String password;

    public abstract List<SecurityRole> getSecurityRoles();

    public boolean hasSecurityRoles(String... roleNames) {
        List<SecurityRole> roles = getSecurityRoles();
        return roles != null && roleNames != null
            && Arrays.stream(roleNames).anyMatch(roleName -> roles.stream().anyMatch(role -> role.name().equals(roleName)));
    }

}