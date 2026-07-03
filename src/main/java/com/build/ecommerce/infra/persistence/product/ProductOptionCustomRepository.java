package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOption;

import java.util.List;

interface ProductOptionCustomRepository {

    List<ProductOption> findAllWithValuesByProductId(Long productId);
}
