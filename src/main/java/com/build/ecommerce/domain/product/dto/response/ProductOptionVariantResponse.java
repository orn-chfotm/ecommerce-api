package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductOptionVariantResponse(
        @Schema(description = "옵션 조합 ID(PK)")
        Long productOptionVariantId,
        @Schema(description = "SKU 코드")
        String sku,
        @Schema(description = "옵션 조합 재고 수량")
        Integer stockQuantity,
        @Schema(description = "옵션 조합 추가 금액")
        BigDecimal priceDelta,
        @Schema(description = "판매 활성화 여부")
        Boolean active,
        @Schema(description = "옵션 조합별 최대 구매 수량")
        Integer maxPurchaseQuantity,
        @Schema(description = "옵션 조합을 구성하는 옵션 값 목록")
        List<ProductOptionVariantValueResponse> optionValues
) {
    public static ProductOptionVariantResponse toDto(ProductOptionVariant productOptionVariant) {
        List<ProductOptionVariantValueResponse> optionValues = productOptionVariant.getProductOptionVariantValues().stream()
                .map(ProductOptionVariantValueResponse::toDto)
                .toList();

        return ProductOptionVariantResponse.builder()
                .productOptionVariantId(productOptionVariant.getId())
                .sku(productOptionVariant.getSku())
                .stockQuantity(productOptionVariant.getStockQuantity())
                .priceDelta(productOptionVariant.getPriceDelta())
                .active(productOptionVariant.isActive())
                .maxPurchaseQuantity(productOptionVariant.getMaxPurchaseQuantity())
                .optionValues(optionValues)
                .build();
    }
}
