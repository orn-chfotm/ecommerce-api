package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponse(
        @Schema(description = "제품 카테고리")
        String category,
        @Schema(description = "제품명")
        String name,
        @Schema(description = "제품 설명")
        String description,
        @Schema(description = "제품 가격")
        BigDecimal price,
        @Schema(description = "제품 재고 수량")
        Integer stockQuantity,
        @Schema(description = "최소 주문 수량")
        int minOrderQuantity,
        @Schema(description = "제품 노출 여부")
        boolean active
) {

    @Builder
    public ProductResponse {
    }

    public static ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .category(product.getCategory().getValue())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .minOrderQuantity(product.getMinOrderQuantity())
                .active(product.isActive())
                .build();
    }
}
