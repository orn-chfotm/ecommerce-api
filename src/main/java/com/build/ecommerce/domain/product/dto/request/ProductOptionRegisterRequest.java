package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProductOptionRegisterRequest(
        @NotEmpty(message = "옵션 명을 하나 이상 등록해야 합니다.")
        @Valid
        @Schema(description = "옵션 명 목록 (예: 색상, 사이즈)")
        List<ProductOptionAxisRequest> options,

        @NotEmpty(message = "옵션 조합을 하나 이상 등록해야 합니다.")
        @Valid
        @Schema(description = "옵션 조합(SKU) 목록")
        List<ProductOptionVariantRequest> variants
) {
}
