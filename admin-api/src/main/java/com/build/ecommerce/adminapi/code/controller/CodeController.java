package com.build.ecommerce.adminapi.code.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailUpdateRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupSortOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupUpdateRequest;
import com.build.ecommerce.domain.code.dto.response.*;
import com.build.ecommerce.domain.code.service.CodeService;
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
@RequestMapping("/v1/code-groups")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "코드 관리 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CodeGroupResponse.class)
        )
)
@PreAuthorize("hasRole('ADMIN')")
public class CodeController {

    private final CodeService codeService;

    @PostMapping
    @Operation(method = "POST", summary = "Register Code Group", description = "그룹 코드를 등록합니다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> registerCodeGroup(
            @Valid @RequestBody CodeGroupRegisterRequest request
    ) {
        return SuccessResponse.toResponse(codeService.registerCodeGroup(request));
    }

    @GetMapping
    @Operation(method = "GET", summary = "get Code Tree", description = "코드 그룹과 상세 코드를 트리 형태로 조회합니다.")
    public ResponseEntity<SuccessResponse<List<CodeTreeResponse>>> getCodeTree() {
        return SuccessResponse.toResponse(codeService.getCodeTree());
    }

    @GetMapping("/{codeGroupId}")
    @Operation(method = "GET", summary = "get Code Group Detail", description = "코드 그룹 상세 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> getCodeGroupDetail(
            @PathVariable Long codeGroupId
    ) {
        return SuccessResponse.toResponse(codeService.getCodeGroupDetail(codeGroupId));
    }

    @PostMapping("/{codeGroupId}/code-details")
    @Operation(method = "POST", summary = "Register Code Detail", description = "상세 코드를 등록합니다. 정렬 순서는 지정하지 않으며, 항상 형제 중 맨 끝에 추가됩니다.")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> registerCodeDetail(
            @PathVariable Long codeGroupId,
            @Valid @RequestBody CodeDetailRegisterRequest request
    ) {
        return SuccessResponse.toResponse(codeService.registerCodeDetail(codeGroupId, request));
    }

    @GetMapping("/{codeGroupId}/code-details/{codeDetailId}")
    @Operation(method = "GET", summary = "get Code Detail", description = "상세 코드 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> getCodeDetailDetail(
            @PathVariable Long codeGroupId,
            @PathVariable Long codeDetailId
    ) {
        return SuccessResponse.toResponse(codeService.getCodeDetailDetail(codeDetailId));
    }

    @PatchMapping("/{codeGroupId}")
    @Operation(method = "PATCH", summary = "update Code Group", description = "코드 그룹을 정보를 수정한다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> updateCodeGroup(
            @PathVariable Long codeGroupId,
            @Valid @RequestBody CodeGroupUpdateRequest request
    ) {
        return SuccessResponse.toResponse(codeService.updateCodeGroup(codeGroupId, request));
    }

    @PatchMapping("/{codeGroupId}/sort-order")
    @Operation(method = "PATCH", summary = "Move Code Group Order", description = "코드 그룹을 원하는 순서로 이동합니다(사이 구간은 자동으로 밀림).")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> moveCodeGroupSortOrder(
            @PathVariable Long codeGroupId,
            @Valid @RequestBody CodeGroupSortOrderMoveRequest request
    ) {
        return SuccessResponse.toResponse(codeService.moveCodeGroup(codeGroupId, request));
    }

    @PatchMapping("/{codeGroupId}/code-details/{codeDetailId}")
    @Operation(method = "PATCH", summary = "update Code Detail", description = "코드 상세 정보를 수정한다.")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> updateCodeDetail(
            @PathVariable Long codeGroupId,
            @PathVariable Long codeDetailId,
            @Valid @RequestBody CodeDetailUpdateRequest request
    ) {
        return SuccessResponse.toResponse(codeService.updateCodeDetail(codeGroupId, codeDetailId, request));
    }

    @PatchMapping("/{codeGroupId}/code-details/{codeDetailId}/sort-order")
    @Operation(method = "PATCH", summary = "Move Code Detail Order", description = "상세 코드를 원하는 순서로 이동합니다(사이 구간은 자동으로 밀림). targetParentId를 지정하면 같은 그룹 내 다른 최상위 상세 코드의 자식으로 옮길 수 있습니다(자식이 있는 코드, 최상위가 아닌 부모로의 이동은 불가).")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> moveCodeDetailSortOrder(
            @PathVariable Long codeGroupId,
            @PathVariable Long codeDetailId,
            @Valid @RequestBody CodeDetailSortOrderMoveRequest request
    ) {
        return SuccessResponse.toResponse(codeService.moveCodeDetail(codeGroupId, codeDetailId, request));
    }

    @DeleteMapping("/{codeGroupId}")
    @Operation(method = "DELETE", summary = "Delete Code Group", description = "코드 그룹을 삭제합니다. 하위 상세 코드가 있으면 삭제할 수 없습니다.")
    public ResponseEntity<SuccessResponse<Void>> deleteCodeGroup(
            @PathVariable Long codeGroupId
    ) {
        codeService.deleteCodeGroup(codeGroupId);
        return SuccessResponse.toResponse(null);
    }

    @DeleteMapping("/{codeGroupId}/code-details/{codeDetailId}")
    @Operation(method = "DELETE", summary = "Delete Code Detail", description = "상세 코드를 삭제합니다. 자식 상세 코드가 있으면 삭제할 수 없습니다.")
    public ResponseEntity<SuccessResponse<Void>> deleteCodeDetail(
            @PathVariable Long codeGroupId,
            @PathVariable Long codeDetailId
    ) {
        codeService.deleteCodeDetail(codeGroupId, codeDetailId);
        return SuccessResponse.toResponse(null);
    }
}
