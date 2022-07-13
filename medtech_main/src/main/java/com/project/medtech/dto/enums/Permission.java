package com.project.medtech.dto.enums;

public enum Permission {

    USER_WRITE("user:write"),
    USER_READ("user:read"),
    USER_FORGOT_PASSWORD("user:forgot_password");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
