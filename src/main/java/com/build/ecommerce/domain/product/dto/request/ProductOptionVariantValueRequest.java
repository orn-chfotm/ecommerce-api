package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ProductOptionVariantValueRequest(
        @NotBlank(message = "옵션 명을 입력해야 합니다.")
        @Schema(description = "옵션 명 (예: 색상)")
        String optionName,

        @NotBlank(message = "옵션 값을 입력해야 합니다.")
        @Schema(description = "옵션 값 (예: 블랙)")
        String value
) {
}
