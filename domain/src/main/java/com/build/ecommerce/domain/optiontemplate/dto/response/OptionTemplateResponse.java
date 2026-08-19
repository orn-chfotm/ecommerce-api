package com.build.ecommerce.domain.optiontemplate.dto.response;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OptionTemplateResponse(
        @Schema(description = "옵션 템플릿 ID(PK)")
        Long optionTemplateId,
        @Schema(description = "옵션 템플릿 명")
        String name,
        @Schema(description = "옵션 값 목록")
        List<OptionTemplateValueResponse> optionTemplateValues
) {
    public static OptionTemplateResponse toDto(OptionTemplate optionTemplate) {
        List<OptionTemplateValueResponse> values = optionTemplate.getOptionTemplateValues().stream()
                .map(OptionTemplateValueResponse::toDto)
                .toList();

        return OptionTemplateResponse.builder()
                .optionTemplateId(optionTemplate.getId())
                .name(optionTemplate.getName())
                .optionTemplateValues(values)
                .build();
    }

    public static OptionTemplateResponse toCreateDto(Long optionTemplateId) {
        return OptionTemplateResponse.builder()
                .optionTemplateId(optionTemplateId)
                .build();
    }
}
