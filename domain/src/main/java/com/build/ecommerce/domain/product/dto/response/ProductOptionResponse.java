package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.ProductOption;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductOptionResponse(
        @Schema(description = "옵션 명 ID(PK)")
        Long productOptionId,
        @Schema(description = "옵션 명 명")
        String name,
        @Schema(description = "정렬 순서")
        Integer sortOrder,
        @Schema(description = "옵션 값 목록")
        List<ProductOptionValueResponse> values
) {
    public static ProductOptionResponse toDto(ProductOption productOption) {
        List<ProductOptionValueResponse> values = productOption.getProductOptionValues().stream()
                .map(ProductOptionValueResponse::toDto)
                .toList();

        return ProductOptionResponse.builder()
                .productOptionId(productOption.getId())
                .name(productOption.getName())
                .sortOrder(productOption.getSortOrder())
                .values(values)
                .build();
    }
}
