package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductStatusType {
    SELLING("판매중"),
    SOLD_OUT("매진"),
    STOPPED("팬매 중지"),
    DELETED("삭제");

    ProductStatusType(String description) {
        this.description = description;
    }

    private String description;

    public static ProductStatusType getByValue(String category) {
        return Arrays.stream(ProductStatusType.values())
                .filter(val -> val.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(ProductExceptionCode.PRODUCT_CATEGORY_NOT_FOUND));
    }
}
