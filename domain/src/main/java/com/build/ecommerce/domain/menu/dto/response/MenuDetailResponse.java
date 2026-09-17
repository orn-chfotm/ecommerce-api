package com.build.ecommerce.domain.menu.dto.response;

import com.build.ecommerce.domain.menu.entity.MenuDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MenuDetailResponse(
        @Schema(name = "메뉴 상세 id")
        Long id,
        @Schema(name = "메뉴 상세 명")
        String name,
        @Schema(name = "메뉴 상세 URL")
        String url,
        @Schema(name = "정렬 순서")
        int sortOrder,
        @Schema(name = "사용 여부")
        boolean active,
        @Schema(name = "트리 깊이")
        int depth
) {
        public static MenuDetailResponse toDto(MenuDetail menuDetail) {
                return MenuDetailResponse.builder()
                        .id(menuDetail.getId())
                        .name(menuDetail.getName())
                        .url(menuDetail.getUrl())
                        .sortOrder(menuDetail.getSortOrder())
                        .active(menuDetail.isActive())
                        .depth(menuDetail.getDepth())
                .build();
        }
}
