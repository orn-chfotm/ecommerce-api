package com.build.ecommerce.domain.product.enums;

import lombok.Getter;

@Getter
public enum ProductType {
    NORMAL("일반 상품"),
    ADD_ON("추가구성상품");

    ProductType(String description) {
        this.description = description;
    }

    private final String description;
}
