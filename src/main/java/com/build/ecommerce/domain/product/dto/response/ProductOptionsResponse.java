package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductOptionsResponse(
        @Schema(description = "옵션 등록 여부")
        Boolean hasOptions,
        @Schema(description = "옵션 명 목록")
        List<ProductOptionResponse> options,
        @Schema(description = "옵션 조합(SKU) 목록")
        List<ProductOptionVariantResponse> variants
) {
    public static ProductOptionsResponse toDto(Product product, List<ProductOptionResponse> options, List<ProductOptionVariantResponse> variants) {
        return ProductOptionsResponse.builder()
                .hasOptions(product.isHasOptions())
                .options(options)
                .variants(variants)
                .build();
    }
}
