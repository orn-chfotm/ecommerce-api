package com.build.ecommerce.infra.persistence.cart;

import com.build.ecommerce.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"product"})
    List<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"product"})
    Optional<Cart> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"product"})
    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("delete from Cart c where c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
