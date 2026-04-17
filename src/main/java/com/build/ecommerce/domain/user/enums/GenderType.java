package com.build.ecommerce.domain.user.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GenderType {
    MAN("M"), WOMAN("W");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }

    public static GenderType getByValue(String value) {
        return Arrays.stream(GenderType.values())
                .filter(val -> val.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gender value is not found"));
    }
}
