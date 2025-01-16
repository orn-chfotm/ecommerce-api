package com.build.ecommerce.domain.order.dto.request;

import com.build.ecommerce.domain.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        @Schema(description = "주문번호")
        Long orderId,
        @Schema(description = "주문상태")
        String status,
        @Schema(description = "주문 내역 상세")
        List<OrderedDetail> orderedDetail
) {

    public static class OrderResponseBuilder {
        private Long orderId;
        private String status;
        private List<OrderedDetail> orderedDetail = new ArrayList<>();

        public OrderResponseBuilder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public OrderResponseBuilder orderedDetail(List<OrderedDetail> orderedDetail) {
            this.orderedDetail = orderedDetail;
            return this;
        }

        public OrderResponse build() {
            return new OrderResponse(orderId, status, orderedDetail);
        }
    }

    public static OrderResponseBuilder builder() {
        return new OrderResponseBuilder();
    }

    public static OrderResponse toDto(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
            .build();
    }

    public static OrderResponse toOrderedDetailDto(Order order, List<OrderedDetail> orderedDetails) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .orderedDetail(orderedDetails)
                .build();
    }
}
