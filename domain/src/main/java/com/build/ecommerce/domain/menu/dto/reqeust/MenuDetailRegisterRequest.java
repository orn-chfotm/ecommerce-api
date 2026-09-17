package com.build.ecommerce.domain.menu.dto.reqeust;

import com.build.ecommerce.domain.menu.entity.MenuDetail;
import com.build.ecommerce.domain.menu.entity.MenuGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuDetailRegisterRequest(
        @Schema(name = "부모 메뉴 상세 id (최상위인 경우 null)")
        Long parentId,
        @NotBlank(message = "메뉴 상세 명을 확인해주세요.")
        @Size(min = 1, max = 100, message = "메뉴 상세 명은 최소 1자 이상 100자 이하를 입력해주세요.")
        @Schema(name = "메뉴 상세 명")
        String name,
        @Size(max = 200, message = "URL은 200자 이하로 입력해주세요.")
        @Schema(name = "메뉴 상세 URL")
        String url,
        @NotNull(message = "사용 여부를 확인해주세요.")
        @Schema(name = "사용 여부")
        Boolean active
) {
        public MenuDetail toEntity(MenuGroup menuGroup, MenuDetail parent, int sortOrder) {
                return MenuDetail.builder()
                        .menuGroup(menuGroup)
                        .parent(parent)
                        .name(this.name)
                        .url(this.url)
                        .sortOrder(sortOrder)
                        .active(this.active)
                        .build();
        }
}
