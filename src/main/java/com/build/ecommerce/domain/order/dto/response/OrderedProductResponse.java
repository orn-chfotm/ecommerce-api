package com.build.ecommerce.domain.order.dto.response;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderedProductResponse(
        @Schema(description = "주문 수량")
        Integer quantity,
        @Schema(description = "주문 금액")
        BigDecimal totalPrice
) {

        private static class OrderProductResponseBuilder {
                private Integer quantity;
                private BigDecimal totalPrice;

                private OrderProductResponseBuilder quantity(Integer quantity) {
                        this.quantity = quantity;
                        return this;
                }

                private OrderProductResponseBuilder totalPrice(BigDecimal totalPrice) {
                        this.totalPrice = totalPrice;
                        return this;
                }

                private OrderedProductResponse build() {
                        return new OrderedProductResponse(quantity, totalPrice);
                }
        }

        public static OrderProductResponseBuilder builder() {
                return new OrderProductResponseBuilder();
        }

        public static OrderedProductResponse toDto(OrderProduct orderProduct) {
                return OrderedProductResponse.builder()
                        .quantity(orderProduct.getQuantity())
                        .totalPrice(orderProduct.getTotalPrice())
                        .build();
        }
}
