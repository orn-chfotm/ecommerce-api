package com.build.ecommerce.domain.admin.controller;

import com.build.ecommerce.core.web.dto.SuccessResponse;
import com.build.ecommerce.domain.admin.dto.request.AdminRequest;
import com.build.ecommerce.domain.admin.dto.response.AdminResponse;
import com.build.ecommerce.domain.admin.exception.AdminExistException;
import com.build.ecommerce.domain.admin.exception.AdminNotFountException;
import com.build.ecommerce.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "관리자 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AdminResponse.class)
        )
)
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @Secured("ROLE_ADMIN")
    @Operation(method = "GET", summary = "get admin infomation" , description = "관리자 정보를 조회합니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "404", description = "관리자 정보를 찾을 수 없습니다.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdminNotFountException.class)
                            )
                    )
            }
    )
    public ResponseEntity<SuccessResponse<AdminResponse>> getAdminDetail(@Valid @RequestBody AdminRequest request) {
        return SuccessResponse.toResponse(adminService.getAdminDetail(request));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    @Operation(method = "POST", summary = "register Admin", description = "관리자를 등록합니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 관리자 Email 입니다.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdminExistException.class)
                            )
                    )
            }
    )
    public ResponseEntity<SuccessResponse<AdminResponse>> registerAdmin(@Valid @RequestBody AdminRequest request) {
        return SuccessResponse.toResponse(adminService.registerAdmin(request));
    }
}


