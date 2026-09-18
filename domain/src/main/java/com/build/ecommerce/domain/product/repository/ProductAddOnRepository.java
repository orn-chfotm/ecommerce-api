package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductAddOn;

import java.util.List;
import java.util.Optional;

public interface ProductAddOnRepository {

    ProductAddOn save(ProductAddOn productAddOn);

    List<ProductAddOn> findAllByProductId(Long productId);

    boolean existsByProductIdAndAddOnProductId(Long productId, Long addOnProductId);

    Optional<Integer> findMaxSortOrderByProductId(Long productId);

    List<ProductAddOn> findAllByProductIdAndAddOnProductIdIn(Long productId, List<Long> addOnProductIds);
}
