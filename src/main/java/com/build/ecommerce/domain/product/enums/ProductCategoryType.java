package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.common.exception.InvalidInputException;
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
                .orElseThrow(() -> new InvalidInputException("제품 카테고리 정보를 찾을 수 없습니다."));
    }
}
