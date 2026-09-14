package com.build.ecommerce.domain.code.dto.response;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CodeDetailResponse(
        @Schema(name = "코드 id")
        Long id,
        @Schema(name = "코드")
        String code,
        @Schema(name = "코드 명")
        String name,
        @Schema(name = "정렬 순서")
        int sortOrder,
        @Schema(name = "사용 여부")
        boolean active,
        @Schema(name = "트리 깊이")
        int depth
) {
        public static CodeDetailResponse toDto(CodeDetail codeDetail) {
                return CodeDetailResponse.builder()
                        .id(codeDetail.getId())
                        .code(codeDetail.getCode())
                        .name(codeDetail.getName())
                        .sortOrder(codeDetail.getSortOrder())
                        .active(codeDetail.isActive())
                        .depth(codeDetail.getDepth())
                .build();
        }
}
