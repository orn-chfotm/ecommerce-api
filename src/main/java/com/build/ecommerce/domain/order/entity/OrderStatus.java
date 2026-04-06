package com.build.ecommerce.domain.order.entity;

import java.util.List;

public enum OrderStatus {
    COMPLETE("성공"),
    FAIL("실패"),
    CANCEL("취소"),
    RELEASE("출고"),
    DELIVERY("배송중");

    OrderStatus(String value) {
        this.value = value;
    }

    private String value;

    public static List<OrderStatus> getCancelable() {
        return List.of(COMPLETE);
    }
}
