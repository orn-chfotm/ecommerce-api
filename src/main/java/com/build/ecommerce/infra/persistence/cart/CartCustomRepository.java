package com.build.ecommerce.infra.persistence.cart;

import com.build.ecommerce.domain.cart.entity.Cart;

import java.util.Optional;

interface CartCustomRepository {

    void deleteAllByUserId(Long userId);

    Optional<Cart> findByUserIdAndProductIdAndVariantId(Long userId, Long productId, Long variantId);
}
