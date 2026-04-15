package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductWishRepository extends JpaRepository<ProductWish, Long> {

    List<ProductWish> findByUserId(Long userId);

    Optional<ProductWish> findByIdAndUserId(Long id, Long userId);

    int deleteProductWishByIdAndUserId(Long id, Long userId);
}
