package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductOption;

import java.util.List;

public interface ProductOptionRepository {

    ProductOption save(ProductOption productOption);

    List<ProductOption> findAllWithValuesByProductId(Long productId);
}
