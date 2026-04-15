package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.core.web.dto.SuccessResponse;
import com.build.ecommerce.domain.product.dto.request.ProductWishRequest;
import com.build.ecommerce.domain.product.dto.response.ProductWishResponse;
import com.build.ecommerce.domain.product.service.ProductWishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/v1/wish")
@RequiredArgsConstructor
@Tag(name = "제품 찜하기", description = "제품 찜하기 관련 API")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductWishResponse.class)
        )
)
public class ProductWishController {

    private final ProductWishService service;

    @PostMapping
    @Operation(method = "POST", summary = "제품 찜하기 등록", description = "선택한 제품을 찜 목록에 등록한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "404", description = "제품/사용자를 찾을 수 없는 경우")
            }
    )
    public ResponseEntity<SuccessResponse<ProductWishResponse>> registerProductWish(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProductWishRequest request
    ) {
        return SuccessResponse.toResponse(service.registerProductWish(userId, request));
    }

    @GetMapping
    @Operation(method = "GET", summary = "제품 찜하기 리스트 조회", description = "선택한 제품들의 리스트를 조회한다")
    public ResponseEntity<SuccessResponse<List<ProductWishResponse>>> selectProductWishList(
            @AuthenticationPrincipal Long userId
    ) {
        return SuccessResponse.toResponse(service.selectProductWishList(userId));
    }

    @GetMapping("/{productWishId}")
    public ResponseEntity<SuccessResponse<ProductWishResponse>> selectProductWishDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productWishId
    ) {
        return SuccessResponse.toResponse(service.selectProductWishDetail(userId, productWishId));
    }

    @DeleteMapping("/{productWishId}")
    public ResponseEntity<SuccessResponse<ProductWishResponse>> deleteProductWish(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productWishId
    ) {
        return SuccessResponse.toResponse(service.deleteProductWish(userId, productWishId));
    }
}
