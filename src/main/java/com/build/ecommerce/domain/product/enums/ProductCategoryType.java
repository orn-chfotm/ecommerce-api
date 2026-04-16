package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.core.web.exception.InvalidInputException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductCategoryType {
    FASHION("fashion"),
    BEAUTY("beauty"),
    FOOD("food"),
    DIGITAL("digital"),
    TOY("toy");

    private final String value;

    ProductCategoryType(String value) {
        this.value = value;
    }

    public static ProductCategoryType getByValue(String category) {
        return Arrays.stream(ProductCategoryType.values())
                .filter(val -> val.getValue().equals(category))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("제품 카테고리 정보를 찾을 수 없습니다."));
    }
}
