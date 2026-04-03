package com.build.ecommerce.domain.address.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AddressType {
    REGION_ADDR("지번 주소"),
    ROAD_ADDR("도로명 주소");

    private String description;

    AddressType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static AddressType from(String value) {
        for (AddressType addressType : values()) {
            if (addressType.name().equals(value)) return addressType;
        }

        return null;
    }
}
