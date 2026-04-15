package com.build.ecommerce.domain.order.dto.response;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OrderedProductResponse(
        @Schema(description = "주문 수량")
        Integer quantity,
        @Schema(description = "주문 금액")
        BigDecimal totalPrice
) {
        public static OrderedProductResponse toDto(OrderProduct orderProduct) {
                return OrderedProductResponse.builder()
                        .quantity(orderProduct.getQuantity())
                        .totalPrice(orderProduct.getTotalPrice())
                        .build();
        }
}
