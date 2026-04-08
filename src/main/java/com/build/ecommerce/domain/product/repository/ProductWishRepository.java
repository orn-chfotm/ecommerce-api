package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductWishRepository extends JpaRepository<ProductWish, Long> {
}
