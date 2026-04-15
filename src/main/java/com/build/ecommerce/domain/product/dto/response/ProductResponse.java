package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        @Schema(description = "제품 ID(PK)")
        Long productId,
        @Schema(description = "제품 카테고리")
        ProductCategoryType category,
        @Schema(description = "제품명")
        String name,
        @Schema(description = "제품 설명")
        String description,
        @Schema(description = "제품 가격")
        BigDecimal price,
        @Schema(description = "제품 재고 수량")
        Integer stockQuantity,
        @Schema(description = "최소 주문 수량")
        Integer minOrderQuantity,
        @Schema(description = "제품 노출 여부")
        Boolean active
) {
    public static ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .category(product.getCategory())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .minOrderQuantity(product.getMinOrderQuantity())
                .active(product.isActive())
                .build();
    }

    public static ProductResponse toCreateDto(Long productId) {
        return ProductResponse.builder()
                .productId(productId)
                .build();
    }
}
