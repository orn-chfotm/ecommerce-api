package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductWish;

import java.util.List;
import java.util.Optional;

public interface ProductWishRepository {

    ProductWish save(ProductWish productWish);

    List<ProductWish> findByUserId(Long userId);

    Optional<ProductWish> findByIdAndUserId(Long id, Long userId);

    void delete(ProductWish productWish);

    int deleteProductWishByIdAndUserId(Long id, Long userId);
}
