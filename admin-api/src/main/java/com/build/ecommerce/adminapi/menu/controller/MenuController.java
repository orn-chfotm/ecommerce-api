package com.build.ecommerce.adminapi.menu.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailUpdateRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupUpdateRequest;
import com.build.ecommerce.domain.menu.dto.response.*;
import com.build.ecommerce.domain.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/menu-groups")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "메뉴 관리 관련 Api")
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "POST", summary = "Register Menu Group", description = "메뉴 그룹을 등록합니다.")
    public ResponseEntity<SuccessResponse<MenuGroupResponse>> registerMenuGroup(
            @Valid @RequestBody MenuGroupRegisterRequest request
    ) {
        return SuccessResponse.toResponse(menuService.registerMenuGroup(request));
    }

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

    @PatchMapping("/{menuGroupId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "PATCH", summary = "update Menu Group", description = "메뉴 그룹 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<MenuGroupResponse>> updateMenuGroup(
            @PathVariable Long menuGroupId,
            @Valid @RequestBody MenuGroupUpdateRequest request
    ) {
        return SuccessResponse.toResponse(menuService.updateMenuGroup(menuGroupId, request));
    }

    @PatchMapping("/{menuGroupId}/sort-order")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "PATCH", summary = "Move Menu Group Order", description = "메뉴 그룹을 원하는 순서로 이동합니다(사이 구간은 자동으로 밀림).")
    public ResponseEntity<SuccessResponse<MenuGroupResponse>> moveMenuGroupSortOrder(
            @PathVariable Long menuGroupId,
            @Valid @RequestBody MenuGroupSortOrderMoveRequest request
    ) {
        return SuccessResponse.toResponse(menuService.moveMenuGroup(menuGroupId, request));
    }

    @DeleteMapping("/{menuGroupId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "DELETE", summary = "Delete Menu Group", description = "메뉴 그룹을 삭제합니다. 하위 메뉴 상세가 있으면 삭제할 수 없습니다.")
    public ResponseEntity<SuccessResponse<Void>> deleteMenuGroup(
            @PathVariable Long menuGroupId
    ) {
        menuService.deleteMenuGroup(menuGroupId);
        return SuccessResponse.toResponse(null);
    }

    @PostMapping("/{menuGroupId}/menu-details")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "POST", summary = "Register Menu Detail", description = "메뉴 상세를 등록합니다. 정렬 순서는 지정하지 않으며, 항상 형제 중 맨 끝에 추가됩니다.")
    public ResponseEntity<SuccessResponse<MenuDetailResponse>> registerMenuDetail(
            @PathVariable Long menuGroupId,
            @Valid @RequestBody MenuDetailRegisterRequest request
    ) {
        return SuccessResponse.toResponse(menuService.registerMenuDetail(menuGroupId, request));
    }

    @GetMapping("/{menuGroupId}/menu-details/{menuDetailId}")
    @Operation(method = "GET", summary = "get Menu Detail", description = "메뉴 상세 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<MenuDetailResponse>> getMenuDetailDetail(
            @PathVariable Long menuGroupId,
            @PathVariable Long menuDetailId
    ) {
        return SuccessResponse.toResponse(menuService.getMenuDetailDetail(menuDetailId));
    }

    @PatchMapping("/{menuGroupId}/menu-details/{menuDetailId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "PATCH", summary = "update Menu Detail", description = "메뉴 상세 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<MenuDetailResponse>> updateMenuDetail(
            @PathVariable Long menuGroupId,
            @PathVariable Long menuDetailId,
            @Valid @RequestBody MenuDetailUpdateRequest request
    ) {
        return SuccessResponse.toResponse(menuService.updateMenuDetail(menuGroupId, menuDetailId, request));
    }

    @PatchMapping("/{menuGroupId}/menu-details/{menuDetailId}/sort-order")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "PATCH", summary = "Move Menu Detail Order", description = "메뉴 상세를 원하는 순서로 이동합니다(사이 구간은 자동으로 밀림). targetParentId를 지정하면 같은 그룹 내 다른 최상위 메뉴 상세의 자식으로 옮길 수 있습니다(자식이 있는 메뉴, 최상위가 아닌 부모로의 이동은 불가).")
    public ResponseEntity<SuccessResponse<MenuDetailResponse>> moveMenuDetailSortOrder(
            @PathVariable Long menuGroupId,
            @PathVariable Long menuDetailId,
            @Valid @RequestBody MenuDetailSortOrderMoveRequest request
    ) {
        return SuccessResponse.toResponse(menuService.moveMenuDetail(menuGroupId, menuDetailId, request));
    }

    @DeleteMapping("/{menuGroupId}/menu-details/{menuDetailId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "DELETE", summary = "Delete Menu Detail", description = "메뉴 상세를 삭제합니다. 자식 메뉴 상세가 있으면 삭제할 수 없습니다.")
    public ResponseEntity<SuccessResponse<Void>> deleteMenuDetail(
            @PathVariable Long menuGroupId,
            @PathVariable Long menuDetailId
    ) {
        menuService.deleteMenuDetail(menuGroupId, menuDetailId);
        return SuccessResponse.toResponse(null);
    }
}
