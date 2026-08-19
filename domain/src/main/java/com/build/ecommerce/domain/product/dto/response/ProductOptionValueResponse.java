package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.ProductOptionValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductOptionValueResponse(
        @Schema(description = "옵션 값 ID(PK)")
        Long productOptionValueId,
        @Schema(description = "옵션 값")
        String value,
        @Schema(description = "정렬 순서")
        Integer sortOrder
) {
    public static ProductOptionValueResponse toDto(ProductOptionValue productOptionValue) {
        return ProductOptionValueResponse.builder()
                .productOptionValueId(productOptionValue.getId())
                .value(productOptionValue.getValue())
                .sortOrder(productOptionValue.getSortOrder())
                .build();
    }
}
