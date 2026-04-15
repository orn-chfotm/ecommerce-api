package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductWish;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductWishResponse (
        @Schema(description = "제품 찜하기 PK")
        Long productWishId,
        @Schema(description = "제품 찜하기 - 제품 정보")
        Product product
) {
    public static ProductWishResponse toDto(ProductWish productWish) {
        return ProductWishResponse.builder()
                .productWishId(productWish.getId())
                .product(productWish.getProduct())
                .build();
    }
}