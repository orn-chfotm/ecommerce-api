package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;

import java.util.List;
import java.util.Optional;

public interface ProductOptionVariantRepository {

    ProductOptionVariant save(ProductOptionVariant productOptionVariant);

    Optional<ProductOptionVariant> findById(Long id);

    List<ProductOptionVariant> findAllWithValuesByProductId(Long productId);

    Optional<ProductOptionVariant> findByIdForUpdate(Long id);

    List<ProductOptionVariantValue> findVariantValuesByVariantIds(List<Long> variantIds);
}
