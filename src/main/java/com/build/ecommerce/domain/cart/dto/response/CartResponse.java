package com.build.ecommerce.domain.cart.dto.response;

import com.build.ecommerce.domain.cart.entity.Cart;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartResponse(
        Long cartId,
        Long productId,
        String productName,
        BigDecimal price,
        int cartQuantity,
        Integer stockQuantity
) {
    public static CartResponse toDto(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getName())
                .price(cart.getProduct().getPrice())
                .cartQuantity(cart.getQuantity())
                .stockQuantity(cart.getProduct().getStockQuantity())
                .build();
    }
}
