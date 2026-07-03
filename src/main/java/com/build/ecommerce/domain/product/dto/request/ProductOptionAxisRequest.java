package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductOptionAxisRequest(
        @NotBlank(message = "옵션 명을 입력해야 합니다.")
        @Schema(description = "옵션 명 (예: 색상, 사이즈)")
        String name,

        @NotNull(message = "정렬 순서를 입력해야 합니다.")
        @Min(value = 1, message = "정렬 순서는 {value} 이상을 입력해야 합니다.")
        @Schema(description = "정렬 순서")
        Integer sortOrder,

        @NotEmpty(message = "옵션 값을 하나 이상 입력해야 합니다.")
        @Schema(description = "옵션 값 목록 (예: 블랙, 화이트)")
        List<@NotBlank(message = "옵션 값은 빈 값일 수 없습니다.") String> values
) {
}
