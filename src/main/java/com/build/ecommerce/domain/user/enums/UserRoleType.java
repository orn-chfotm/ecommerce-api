package com.build.ecommerce.domain.user.enums;

public enum UserRoleType {
    USER("사용자");

    private String description;

    UserRoleType(String description) {
        this.description = description;
    }
}
