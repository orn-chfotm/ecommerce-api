package com.build.ecommerce.domain.optiontemplate.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateRequest;
import com.build.ecommerce.domain.optiontemplate.dto.response.OptionTemplateResponse;
import com.build.ecommerce.domain.optiontemplate.service.OptionTemplateService;
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
@RequestMapping("/v1/option-template")
@RequiredArgsConstructor
@Tag(name = "옵션 템플릿", description = "옵션 템플릿 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OptionTemplateResponse.class)
        )
)
public class OptionTemplateController {

    private final OptionTemplateService optionTemplateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "POST", summary = "Insert OptionTemplate", description = "옵션 템플릿을 등록합니다.")
    public ResponseEntity<SuccessResponse<OptionTemplateResponse>> registerOptionTemplate(@Valid @RequestBody OptionTemplateRequest request) {
        return SuccessResponse.toResponse(optionTemplateService.insertOptionTemplate(request));
    }

    @GetMapping
    @Operation(method = "GET", summary = "Select OptionTemplate List", description = "옵션 템플릿 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<List<OptionTemplateResponse>>> getOptionTemplateList() {
        return SuccessResponse.toResponse(optionTemplateService.getOptionTemplateList());
    }

    @GetMapping("/{optionTemplateId}")
    @Operation(method = "GET", summary = "Select OptionTemplate Detail", description = "옵션 템플릿 상세를 조회합니다.")
    public ResponseEntity<SuccessResponse<OptionTemplateResponse>> getOptionTemplateDetail(@PathVariable Long optionTemplateId) {
        return SuccessResponse.toResponse(optionTemplateService.getOptionTemplateDetail(optionTemplateId));
    }

    @PatchMapping("/{optionTemplateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "PATCH", summary = "Update OptionTemplate", description = "옵션 템플릿을 수정합니다.")
    public ResponseEntity<SuccessResponse<OptionTemplateResponse>> updateOptionTemplate(
            @PathVariable Long optionTemplateId,
            @Valid @RequestBody OptionTemplateRequest request) {
        return SuccessResponse.toResponse(optionTemplateService.updateOptionTemplate(optionTemplateId, request));
    }

    @DeleteMapping("/{optionTemplateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(method = "DELETE", summary = "Delete OptionTemplate", description = "옵션 템플릿을 삭제합니다.")
    public ResponseEntity<SuccessResponse<OptionTemplateResponse>> deleteOptionTemplate(@PathVariable Long optionTemplateId) {
        return SuccessResponse.toResponse(optionTemplateService.deleteOptionTemplate(optionTemplateId));
    }
}
