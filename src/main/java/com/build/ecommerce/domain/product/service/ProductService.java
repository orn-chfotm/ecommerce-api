package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse insertProduct(ProductRequest request) {
        Product product = request.toEntity();
        Long insertedProductId = productRepository.save(product).getId();

        return ProductResponse.builder()
                .productId(insertedProductId)
                .build();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductDetail(final Long productId) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return ProductResponse.toDto(findProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductList(ProductSearchRequest searchRequest) {
        List<Product> findProducts = productRepository.searchProducts(
                searchRequest.getCategory(),
                searchRequest.name(),
                searchRequest.minPrice(),
                searchRequest.maxPrice(),
                searchRequest.stockQuantity()
        );

        return findProducts.stream()
                .map(ProductResponse::toDto)
                .collect(Collectors.toList());
    }
}
