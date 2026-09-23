package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductCategoryType {
    FASHION,
    BEAUTY,
    FOOD,
    DIGITAL,
    TOY;

    public static ProductCategoryType getByValue(String category) {
        return Arrays.stream(ProductCategoryType.values())
                .filter(val -> val.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(ProductExceptionCode.PRODUCT_CATEGORY_NOT_FOUND));
    }
}
