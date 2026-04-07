package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductCustomRepository {

    Optional<List<Product>> customFind(ProductSearchRequest productSearchRequest);
}
