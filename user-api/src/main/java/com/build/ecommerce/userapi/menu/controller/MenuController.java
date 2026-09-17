package com.build.ecommerce.userapi.menu.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.menu.dto.response.MenuDetailResponse;
import com.build.ecommerce.domain.menu.dto.response.MenuGroupResponse;
import com.build.ecommerce.domain.menu.dto.response.MenuTreeResponse;
import com.build.ecommerce.domain.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/menu-groups")
@RequiredArgsConstructor
@Tag(name = "메뉴", description = "메뉴 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MenuGroupResponse.class)
        )
)
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @Operation(method = "GET", summary = "get Menu Tree", description = "메뉴 그룹과 메뉴 상세를 트리 형태로 조회합니다.")
    public ResponseEntity<SuccessResponse<List<MenuTreeResponse>>> getMenuTree() {
        return SuccessResponse.toResponse(menuService.getMenuTree());
    }

    @GetMapping("/{menuGroupId}")
    @Operation(method = "GET", summary = "get Menu Group Detail", description = "메뉴 그룹 상세 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<MenuGroupResponse>> getMenuGroupDetail(
            @PathVariable Long menuGroupId
    ) {
        return SuccessResponse.toResponse(menuService.getMenuGroupDetail(menuGroupId));
    }

    @GetMapping("/{menuGroupId}/menu-details/{menuDetailId}")
    @Operation(method = "GET", summary = "get Menu Detail", description = "메뉴 상세 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<MenuDetailResponse>> getMenuDetailDetail(
            @PathVariable Long menuGroupId,
            @PathVariable Long menuDetailId
    ) {
        return SuccessResponse.toResponse(menuService.getMenuDetailDetail(menuDetailId));
    }
}
