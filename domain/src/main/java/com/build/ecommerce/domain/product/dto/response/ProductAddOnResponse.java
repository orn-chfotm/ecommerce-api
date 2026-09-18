package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductAddOn;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductAddOnResponse(
        @Schema(description = "본품-추가구성상품 매핑 ID(PK)")
        Long productAddOnId,
        @Schema(description = "추가구성상품 ID(PK)")
        Long addOnProductId,
        @Schema(description = "추가구성상품 명")
        String name,
        @Schema(description = "추가구성상품 가격")
        BigDecimal price,
        @Schema(description = "추가구성상품 재고 수량 (null 은 미지정)")
        Integer stockQuantity,
        @Schema(description = "노출 정렬 순서")
        Integer sortOrder
) {
    public static ProductAddOnResponse toDto(ProductAddOn productAddOn) {
        Product addOnProduct = productAddOn.getAddOnProduct();

        return ProductAddOnResponse.builder()
                .productAddOnId(productAddOn.getId())
                .addOnProductId(addOnProduct.getId())
                .name(addOnProduct.getName())
                .price(addOnProduct.getPrice())
                .stockQuantity(addOnProduct.getStockQuantity())
                .sortOrder(productAddOn.getSortOrder())
                .build();
    }
}
