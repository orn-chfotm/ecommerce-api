package com.build.ecommerce.domain.cart.repository;

import com.build.ecommerce.domain.cart.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepository {

    Cart save(Cart cart);

    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByIdAndUserId(Long id, Long userId);

    void delete(Cart cart);

    void deleteAllByUserId(Long userId);

    Optional<Cart> findByUserIdAndProductIdAndVariantId(Long userId, Long productId, Long variantId);
}
