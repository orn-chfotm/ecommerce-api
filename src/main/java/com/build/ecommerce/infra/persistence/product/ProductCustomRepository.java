package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;

import java.util.List;

interface ProductCustomRepository {
    List<Product> searchProducts(ProductSearchRequest productSearchRequest);
}
