package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.core.web.dto.SuccessResponse;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import com.build.ecommerce.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    @Operation(method = "POST", summary = "Insert Prodcut", description = "제품을 등록합니다.")
    public ResponseEntity<SuccessResponse<ProductResponse>> registerProduct(@Valid @ModelAttribute ProductRequest request) {
        return SuccessResponse.toResponse(productService.insertProduct(request));
    }

    @GetMapping
    @Operation(method = "GET", summary = "Select Product List Information", description = "제품 리스트를 검색합니다.")
    public ResponseEntity<SuccessResponse<List<ProductResponse>>> getProductList(@Valid @RequestBody ProductSearchRequest searchRequest) {
        return SuccessResponse.toResponse(productService.getProductList(searchRequest));
    }

    @GetMapping("/{productId}")
    @Operation(method = "GET", summary = "Select Product detail Information", description = "제품 상세를 검색합니다.")
    public ResponseEntity<SuccessResponse<ProductResponse>> getProductDetail(@PathVariable Long productId) {
        return SuccessResponse.toResponse(productService.getProductDetail(productId));
    }
}
