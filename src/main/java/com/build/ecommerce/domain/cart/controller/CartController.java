package com.build.ecommerce.domain.cart.controller;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.domain.cart.dto.request.CartRequest;
import com.build.ecommerce.domain.cart.dto.request.CartUpdateRequest;
import com.build.ecommerce.domain.cart.dto.response.CartResponse;
import com.build.ecommerce.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@Tag(name = "장바구니", description = "장바구니 관련 Api")
@Secured("ROLE_USER")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(method = "POST", summary = "장바구니 상품 추가", description = "상품을 장바구니에 추가합니다. 이미 담긴 상품이면 수량을 합산합니다.")
    public ResponseEntity<SuccessResponse<CartResponse>> addCart(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CartRequest request) {
        return SuccessResponse.toResponse(cartService.addCart(userId, request));
    }

    @GetMapping
    @Operation(method = "GET", summary = "장바구니 목록 조회", description = "장바구니 상품 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<List<CartResponse>>> getCarts(
            @AuthenticationPrincipal Long userId) {
        return SuccessResponse.toResponse(cartService.getCarts(userId));
    }

    @PatchMapping("/{cartId}")
    @Operation(method = "PATCH", summary = "장바구니 수량 수정", description = "장바구니 특정 상품의 수량을 수정합니다.")
    public ResponseEntity<SuccessResponse<CartResponse>> updateCart(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long cartId,
            @Valid @RequestBody CartUpdateRequest request) {
        return SuccessResponse.toResponse(cartService.updateCart(userId, cartId, request));
    }

    @DeleteMapping("/{cartId}")
    @Operation(method = "DELETE", summary = "장바구니 상품 단건 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
    public ResponseEntity<SuccessResponse<Void>> removeCart(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long cartId) {
        cartService.removeCart(userId, cartId);
        return SuccessResponse.toResponse(null);
    }

    @DeleteMapping
    @Operation(method = "DELETE", summary = "장바구니 전체 비우기", description = "장바구니의 모든 상품을 삭제합니다.")
    public ResponseEntity<SuccessResponse<Void>> clearCart(
            @AuthenticationPrincipal Long userId) {
        cartService.clearCart(userId);
        return SuccessResponse.toResponse(null);
    }
}
