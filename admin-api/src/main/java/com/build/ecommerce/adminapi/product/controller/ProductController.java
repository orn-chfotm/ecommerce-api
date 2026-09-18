package com.build.ecommerce.adminapi.product.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.product.dto.request.ProductAddOnRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantStockRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.request.ProductUpdateRequest;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantResponse;
import com.build.ecommerce.domain.product.dto.response.ProductOptionsResponse;
import com.build.ecommerce.domain.product.dto.response.ProductAddOnResponse;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import com.build.ecommerce.domain.product.service.ProductAddOnService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Tag(name = "제품", description = "제품 관련 Api")
@PreAuthorize("hasRole('ADMIN')")
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
    private final ProductAddOnService productAddOnService;

    @PostMapping
    @Operation(method = "POST", summary = "Insert Product", description = "제품을 등록합니다.")
    public ResponseEntity<SuccessResponse<ProductResponse>> registerProduct(@Valid @ModelAttribute ProductRequest request) {
        return SuccessResponse.toResponse(productService.insertProduct(request));
    }

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

    @PatchMapping("/{productId}")
    @Operation(method = "Patch", summary = "Update Product detail Information", description = "제품 상세를 수정합니다.")
    public ResponseEntity<SuccessResponse<ProductResponse>> updateProductDetail(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return SuccessResponse.toResponse(productService.updateProductDetail(productId, request));
    }

    @DeleteMapping("/{productId}")
    @Operation(method = "DELETE", summary = "Delete Product", description = "제품을 삭제합니다. (Soft Delete)")
    public ResponseEntity<SuccessResponse<ProductResponse>> deleteProduct(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productService.deleteProduct(productId));
    }

    @PostMapping("/{productId}/options")
    @Operation(method = "POST", summary = "Register Product Options", description = "제품의 옵션 명과 옵션 조합(SKU)을 등록합니다.")
    public ResponseEntity<SuccessResponse<ProductOptionsResponse>> registerProductOptions(
            @PathVariable Long productId,
            @Valid @RequestBody ProductOptionRegisterRequest request) {
        return SuccessResponse.toResponse(productOptionService.registerProductOptions(productId, request));
    }

    @GetMapping("/{productId}/options")
    @Operation(method = "GET", summary = "Select Product Options", description = "제품의 옵션 명과 옵션 조합(SKU) 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<ProductOptionsResponse>> getProductOptions(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productOptionService.getProductOptions(productId));
    }

    @PatchMapping("/{productId}/options/variants/{variantId}/stock")
    @Operation(method = "PATCH", summary = "Update Product Option Variant Stock", description = "옵션 조합(SKU) 단건의 재고 수량을 수정합니다.")
    public ResponseEntity<SuccessResponse<ProductOptionVariantResponse>> updateProductOptionVariantStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductOptionVariantStockRequest request) {
        return SuccessResponse.toResponse(productOptionService.updateVariantStock(productId, variantId, request));
    }

    @PostMapping("/{productId}/add-ons")
    @Operation(method = "POST", summary = "Register Product Add-On", description = "제품에 추가구성상품을 등록합니다.")
    public ResponseEntity<SuccessResponse<ProductAddOnResponse>> registerProductAddOn(
            @PathVariable Long productId,
            @Valid @RequestBody ProductAddOnRequest request) {
        return SuccessResponse.toResponse(productAddOnService.registerAddOn(productId, request));
    }

    @GetMapping("/{productId}/add-ons")
    @Operation(method = "GET", summary = "Select Product Add-Ons", description = "제품에 등록된 추가구성상품 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<List<ProductAddOnResponse>>> getProductAddOns(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productAddOnService.getAddOns(productId));
    }
}
