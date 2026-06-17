package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductWish;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductWishRepository extends JpaRepository<ProductWish, Long> {

    @EntityGraph(attributePaths = "product")
    List<ProductWish> findByUserId(Long userId);

    Optional<ProductWish> findByIdAndUserId(Long id, Long userId);

    int deleteProductWishByIdAndUserId(Long id, Long userId);
}
