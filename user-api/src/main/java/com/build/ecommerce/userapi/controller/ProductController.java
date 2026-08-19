package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.response.ProductOptionsResponse;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import com.build.ecommerce.domain.product.service.ProductOptionService;
import com.build.ecommerce.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Tag(name = "제품", description = "제품 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
        )
)
public class ProductController {

    private final ProductService productService;
    private final ProductOptionService productOptionService;

    @GetMapping
    @Operation(method = "GET", summary = "Select Product List Information", description = "제품 리스트를 검색합니다.")
    public ResponseEntity<SuccessResponse<Page<ProductResponse>>> getProductList(
            @Valid @ModelAttribute ProductSearchRequest searchRequest,
            Pageable pageable) {
        return SuccessResponse.toResponse(productService.getProductList(searchRequest, pageable));
    }

    @GetMapping("/{productId}")
    @Operation(method = "GET", summary = "Select Product detail Information", description = "제품 상세를 검색합니다.")
    public ResponseEntity<SuccessResponse<ProductResponse>> getProductDetail(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productService.getProductDetail(productId));
    }

    @GetMapping("/{productId}/options")
    @Operation(method = "GET", summary = "Select Product Options", description = "제품의 옵션 명과 옵션 조합(SKU) 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<ProductOptionsResponse>> getProductOptions(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productOptionService.getProductOptions(productId));
    }
}
