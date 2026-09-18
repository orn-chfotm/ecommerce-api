package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductAddOnRequest(
        @Schema(description = "추가구성상품 ID(PK)")
        @NotNull(message = "추가구성상품을 선택해야 합니다.")
        Long addOnProductId
) {
}
