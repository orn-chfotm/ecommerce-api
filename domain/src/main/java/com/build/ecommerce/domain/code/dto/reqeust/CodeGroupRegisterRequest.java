package com.build.ecommerce.domain.code.dto.reqeust;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CodeGroupRegisterRequest(
        @NotNull(message = "코드 값을 설정해주세요.")
        @Schema(name = "코드")
        String code,

        @NotNull(message = "코드 명을 설정해주세요.")
        @Schema(name = "코드 명")
        String name,

        @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
        @Schema(name = "정렬 순서")
        int sortOrder,

        @NotNull(message = "사용 여부를 선택해주세요.")
        @Schema(name = "사용 여부")
        boolean active
) {
        public CodeGroup toEntity() {
                return CodeGroup.builder()
                        .code(this.code)
                        .name(this.name)
                        .sortOrder(this.sortOrder)
                        .active(this.active)
                        .build();
        }
}
