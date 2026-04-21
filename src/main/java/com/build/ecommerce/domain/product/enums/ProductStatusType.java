package com.build.ecommerce.domain.product.enums;

import com.build.ecommerce.common.exception.InvalidInputException;
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
                .orElseThrow(() -> new InvalidInputException("제품 카테고리 정보를 찾을 수 없습니다."));
    }
}
