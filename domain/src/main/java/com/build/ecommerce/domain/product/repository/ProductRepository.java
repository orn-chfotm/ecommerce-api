package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Page<Product> searchProducts(ProductSearchRequest productSearchRequest, Pageable pageable);

    Optional<Product> findByIdForUpdate(Long id);
}
