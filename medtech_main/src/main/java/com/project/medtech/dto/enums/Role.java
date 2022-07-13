package com.project.medtech.dto.enums;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.project.medtech.dto.enums.Permission.*;

public enum Role {
    SUPERADMIN(Sets.newHashSet(USER_READ, USER_WRITE, USER_FORGOT_PASSWORD, USER_UPDATE_PASSWORD)),
    ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, USER_FORGOT_PASSWORD, USER_UPDATE_PASSWORD)),
    DOCTOR(Sets.newHashSet(USER_READ, USER_WRITE, USER_FORGOT_PASSWORD, USER_UPDATE_PASSWORD)),
    PATIENT(Sets.newHashSet(USER_READ, USER_WRITE, USER_FORGOT_PASSWORD, USER_UPDATE_PASSWORD));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
