package com.build.ecommerce.adminapi.code.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.response.CodeDetailResponse;
import com.build.ecommerce.domain.code.dto.response.CodeGroupResponse;
import com.build.ecommerce.domain.code.dto.response.CodeTreeResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
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

    @PostMapping("/code-groups")
    @Operation(method = "POST", summary = "Register Code Group", description = "그룹 코드를 등록합니다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> registerCodeGroup(
            @Valid @RequestBody CodeGroupRegisterRequest request
    ) {
        return SuccessResponse.toResponse(codeService.registerCodeGroup(request));
    }

    @GetMapping("/code-groups")
    @Operation(method = "GET", summary = "get Code Tree", description = "코드 그룹과 상세 코드를 트리 형태로 조회합니다.")
    public ResponseEntity<SuccessResponse<List<CodeTreeResponse>>> getCodeTree() {
        return SuccessResponse.toResponse(codeService.getCodeTree());
    }

    @GetMapping("/code-groups/{codeGroupId}")
    @Operation(method = "GET", summary = "get Code Group Detail", description = "코드 그룹 상세 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> getCodeGroupDetail(
            @PathVariable Long codeGroupId
    ) {
        return SuccessResponse.toResponse(codeService.getCodeGroupDetail(codeGroupId));
    }

    @PostMapping("/code-groups/{codeGroupId}/code-details")
    @Operation(method = "POST", summary = "Register Code Detail", description = "상세 코드를 등록합니다.")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> registerCodeDetail(
            @PathVariable Long codeGroupId,
            @Valid @RequestBody CodeDetailRegisterRequest request
    ) {
        return SuccessResponse.toResponse(codeService.registerCodeDetail(codeGroupId, request));
    }

    @GetMapping("/code-groups/{codeGroupId}/code-details/{codeDetailId}")
    @Operation(method = "GET", summary = "get Code Detail", description = "상세 코드 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<CodeDetailResponse>> getCodeDetailDetail(
            @PathVariable Long codeGroupId,
            @PathVariable Long codeDetailId
    ) {
        return SuccessResponse.toResponse(codeService.getCodeDetailDetail(codeDetailId));
    }
}
