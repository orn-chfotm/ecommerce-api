package com.build.ecommerce.domain.optiontemplate.dto.request;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplateValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionTemplateValueRequest(
        @NotBlank(message = "옵션 값을 입력해야 합니다.")
        @Size(max = 100, message = "옵션 값은 {max}자 이하로 입력해야 합니다.")
        @Schema(description = "옵션 값 (예: 블랙, M)")
        String value,

        @NotNull(message = "정렬 순서를 입력해야 합니다.")
        @Min(value = 0, message = "정렬 순서는 {value} 이상을 입력해야 합니다.")
        @Schema(description = "정렬 순서")
        Integer sortOrder
) {
    public OptionTemplateValue toEntity() {
        return OptionTemplateValue.builder()
                .value(value)
                .sortOrder(sortOrder)
                .build();
    }
}
