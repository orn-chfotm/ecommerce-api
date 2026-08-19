package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductOptionVariantValueResponse(
        @Schema(description = "옵션 명")
        String optionName,
        @Schema(description = "옵션 값")
        String value
) {
    public static ProductOptionVariantValueResponse toDto(ProductOptionVariantValue productOptionVariantValue) {
        return ProductOptionVariantValueResponse.builder()
                .optionName(productOptionVariantValue.getProductOption().getName())
                .value(productOptionVariantValue.getProductOptionValue().getValue())
                .build();
    }
}
