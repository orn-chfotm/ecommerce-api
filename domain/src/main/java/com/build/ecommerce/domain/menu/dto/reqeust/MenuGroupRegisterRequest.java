package com.build.ecommerce.domain.menu.dto.reqeust;

import com.build.ecommerce.domain.menu.entity.MenuGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuGroupRegisterRequest(
        @NotBlank(message = "메뉴 그룹 명을 확인해주세요.")
        @Size(min = 1, max = 100, message = "메뉴 그룹 명은 최소 1자 이상 100자 이하를 입력해주세요.")
        @Schema(name = "메뉴 그룹 명")
        String name,
        @Size(max = 200, message = "URL은 200자 이하로 입력해주세요.")
        @Schema(name = "메뉴 그룹 URL")
        String url,
        @NotNull(message = "정렬 순서를 확인해주세요.")
        @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
        @Schema(name = "정렬 순서")
        Integer sortOrder,
        @NotNull(message = "사용 여부를 확인해주세요.")
        @Schema(name = "사용 여부")
        Boolean active
) {
        public MenuGroup toEntity() {
                return MenuGroup.builder()
                        .name(this.name)
                        .url(this.url)
                        .sortOrder(this.sortOrder)
                        .active(this.active)
                        .build();
        }
}
