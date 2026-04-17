package com.build.ecommerce.domain.admin.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AdminRoleType {
    ADMIN,
    SELLER;

    public static AdminRoleType getByValue(String value) {
        return Arrays.stream(AdminRoleType.values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role value is not found"));
    }
}
