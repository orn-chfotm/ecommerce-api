package com.build.ecommerce.infra.persistence.cart;

import com.build.ecommerce.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface CartJpaRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"product", "productOptionVariant"})
    List<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"product", "productOptionVariant"})
    Optional<Cart> findByIdAndUserId(Long id, Long userId);
}
