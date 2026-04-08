package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.domain.product.dto.request.ProductWishRequest;
import com.build.ecommerce.domain.product.dto.response.ProductWishResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.product.exception.ProductNotFountException;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.product.repository.ProductWishRepository;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFountException;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductWishService {

    private final ProductWishRepository productWishRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductWishResponse registProductWish(Long userId, ProductWishRequest request) {
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(ProductNotFountException::new);
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFountException::new);

        ProductWish saveProductWish = ProductWish.builder()
                .user(findUser)
                .product(findProduct)
                .build();
        productWishRepository.save(saveProductWish);

        return ProductWishResponse.toDto(saveProductWish);
    }
}
