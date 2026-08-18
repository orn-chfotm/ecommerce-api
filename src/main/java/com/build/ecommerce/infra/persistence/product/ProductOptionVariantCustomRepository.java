package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;

import java.util.List;
import java.util.Optional;

interface ProductOptionVariantCustomRepository {

    List<ProductOptionVariant> findAllWithValuesByProductId(Long productId);

    Optional<ProductOptionVariant> findByIdForUpdate(Long id);

    List<ProductOptionVariantValue> findVariantValuesByVariantIds(List<Long> variantIds);
}
