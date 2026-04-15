package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.domain.product.exception.ProductValidationException;
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
                .orElseThrow(() -> new ProductValidationException("Product Category is not found."));
    }
}
