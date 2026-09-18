package com.build.ecommerce.domain.order.dto.response;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OrderedProductResponse(
        @Schema(description = "주문 상품 라인 PK")
        Long orderProductId,
        @Schema(description = "추가구성상품 라인이면 소속 본품 라인 PK, 본품 라인이면 null")
        Long parentOrderProductId,
        @Schema(description = "주문 수량")
        Integer quantity,
        @Schema(description = "주문 금액")
        BigDecimal totalPrice
) {
        public static OrderedProductResponse toDto(OrderProduct orderProduct) {
                /* LAZY 프록시에서 getId()만 읽으므로 추가 쿼리가 발생하지 않는다(fetch join 금지). */
                OrderProduct parent = orderProduct.getParentOrderProduct();

                return OrderedProductResponse.builder()
                        .orderProductId(orderProduct.getId())
                        .parentOrderProductId(parent == null ? null : parent.getId())
                        .quantity(orderProduct.getQuantity())
                        .totalPrice(orderProduct.getTotalPrice())
                        .build();
        }
}
