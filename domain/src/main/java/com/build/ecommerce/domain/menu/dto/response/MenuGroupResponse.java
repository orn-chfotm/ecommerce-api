package com.build.ecommerce.domain.menu.dto.response;

import com.build.ecommerce.domain.menu.entity.MenuGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MenuGroupResponse(
        @Schema(name = "메뉴 그룹 id")
        Long id,
        @Schema(name = "메뉴 그룹 명")
        String name,
        @Schema(name = "메뉴 그룹 URL")
        String url,
        @Schema(name = "정렬 순서")
        int sortOrder,
        @Schema(name = "사용 여부")
        boolean active
) {
        public static MenuGroupResponse toDto(MenuGroup menuGroup) {
                return MenuGroupResponse.builder()
                        .id(menuGroup.getId())
                        .name(menuGroup.getName())
                        .url(menuGroup.getUrl())
                        .sortOrder(menuGroup.getSortOrder())
                        .active(menuGroup.isActive())
                .build();
        }
}
