package com.build.ecommerce.domain.optiontemplate.dto.response;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplateValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OptionTemplateValueResponse(
        @Schema(description = "옵션 템플릿 값 ID(PK)")
        Long optionTemplateValueId,
        @Schema(description = "옵션 값")
        String value,
        @Schema(description = "정렬 순서")
        Integer sortOrder
) {
    public static OptionTemplateValueResponse toDto(OptionTemplateValue optionTemplateValue) {
        return OptionTemplateValueResponse.builder()
                .optionTemplateValueId(optionTemplateValue.getId())
                .value(optionTemplateValue.getValue())
                .sortOrder(optionTemplateValue.getSortOrder())
                .build();
    }
}
