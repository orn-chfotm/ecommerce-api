package com.build.ecommerce.domain.order.enums;

import java.util.List;

public enum OrderStatusType {
    COMPLETE("성공"),
    FAIL("실패"),
    CANCEL("취소"),
    RELEASE("출고"),
    DELIVERY("배송중"),
    DELIVERY_COMPLETED("배송 완료");

    OrderStatusType(String description) {
        this.description = description;
    }

    private String description;

    public static List<OrderStatusType> getCancelable() {
        return List.of(COMPLETE);
    }
}
