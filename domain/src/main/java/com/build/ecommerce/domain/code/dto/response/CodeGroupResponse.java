package com.build.ecommerce.domain.code.dto.response;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CodeGroupResponse(
        @Schema(name = "코드 id")
        Long id,
        @Schema(name = "코드")
        String code,
        @Schema(name = "코드 명")
        String name,
        @Schema(name = "정렬 순서")
        int sortOrder,
        @Schema(name = "사용 여부")
        boolean active
) {
        public static CodeGroupResponse toDto(CodeGroup codeGroup) {
                return CodeGroupResponse.builder()
                        .id(codeGroup.getId())
                        .code(codeGroup.getCode())
                        .name(codeGroup.getName())
                        .sortOrder(codeGroup.getSortOrder())
                        .active(codeGroup.isActive())
                .build();
        }
}
