package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductOptionVariantStockRequest(
        @NotNull(message = "재고 수량을 입력해야 합니다.")
        @Min(value = 0, message = "재고 수량은 {value} 이상을 입력해야 합니다.")
        @Schema(description = "변경할 옵션 조합 재고 수량")
        Integer stockQuantity
) {
}
