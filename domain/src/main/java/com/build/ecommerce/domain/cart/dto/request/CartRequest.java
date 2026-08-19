package com.build.ecommerce.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartRequest(
        @NotNull Long productId,
        @Schema(description = "제품 옵션 조합(SKU) PK, 옵션이 등록된 상품이면 필수")
        Long productOptionVariantId,
        @NotNull @Positive int quantity
) {
}
