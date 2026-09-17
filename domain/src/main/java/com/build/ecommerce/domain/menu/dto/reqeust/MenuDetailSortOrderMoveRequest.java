package com.build.ecommerce.domain.menu.dto.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MenuDetailSortOrderMoveRequest(
        @NotNull(message = "이동할 순서를 확인해주세요.")
        @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
        @Schema(name = "이동할 목표 순서")
        Integer targetSortOrder,
        @Schema(name = "이동할 목표 부모 메뉴 상세 id (생략 시 현재 부모 유지, 최상위로의 이동은 지원하지 않음)")
        Long targetParentId
) {
}
