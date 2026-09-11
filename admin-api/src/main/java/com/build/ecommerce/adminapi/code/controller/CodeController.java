package com.build.ecommerce.adminapi.code.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupSearchRequest;
import com.build.ecommerce.domain.code.dto.response.CodeGroupResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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

    @GetMapping("/code-group-list")
    @Operation(method = "GET", summary = "get Code Group List", description = "그룹 코드 리스트를 조회합니다.")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> getCodeGroupList(
            @Valid @RequestBody CodeGroupSearchRequest request
    ) {
        return SuccessResponse.toResponse(codeService.getCodeGroupList(request));
    }

    @GetMapping("/code-group-detail")
    public ResponseEntity<SuccessResponse<CodeGroupResponse>> getCodeGroupDetail(
            @Valid @RequestBody CodeGroupSearchRequest request
    ) {
        return SuccessResponse.toResponse(codeService.getCodeGroupDetail(request));
    }
}
