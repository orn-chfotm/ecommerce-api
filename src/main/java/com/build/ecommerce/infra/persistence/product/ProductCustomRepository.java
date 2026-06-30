package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface ProductCustomRepository {
    Page<Product> searchProducts(ProductSearchRequest productSearchRequest, Pageable pageable);
}
