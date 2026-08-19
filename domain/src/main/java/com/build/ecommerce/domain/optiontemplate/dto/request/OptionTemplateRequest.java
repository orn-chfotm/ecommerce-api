package com.build.ecommerce.domain.optiontemplate.dto.request;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OptionTemplateRequest(
        @NotBlank(message = "옵션 템플릿 명을 입력해야 합니다.")
        @Size(max = 100, message = "옵션 템플릿 명은 {max}자 이하로 입력해야 합니다.")
        @Schema(description = "옵션 템플릿 명 (예: 사이즈, 색상)")
        String name,

        @NotEmpty(message = "옵션 값을 하나 이상 등록해야 합니다.")
        @Valid
        @Schema(description = "옵션 값 목록")
        List<OptionTemplateValueRequest> optionTemplateValues
) {
    public OptionTemplate toEntity() {
        OptionTemplate optionTemplate = OptionTemplate.builder()
                .name(name)
                .build();

        optionTemplateValues.forEach(valueRequest ->
                optionTemplate.addOptionTemplateValue(valueRequest.toEntity()));

        return optionTemplate;
    }
}
