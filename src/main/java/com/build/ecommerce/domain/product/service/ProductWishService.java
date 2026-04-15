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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductWishService {

    private final ProductWishRepository productWishRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductWishResponse registerProductWish(Long userId, ProductWishRequest request) {
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(ProductNotFountException::new);
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFountException::new);

        ProductWish saveProductWish = ProductWish.builder()
                .user(findUser)
                .product(findProduct)
                .build();

        return ProductWishResponse.toDto(productWishRepository.save(saveProductWish));
    }

    @Transactional(readOnly = true)
    public List<ProductWishResponse> selectProductWishList(final Long userId) {
        List<ProductWish> wishList = productWishRepository.findByUserId(userId);

        return wishList.stream()
                .map(ProductWishResponse::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductWishResponse selectProductWishDetail(final Long userId, final Long productWishId) {
        ProductWish findProductWish = productWishRepository.findByIdAndUserId(productWishId, userId)
                .orElseThrow(() -> new ProductNotFountException("찜한 제품을 찾을 수 없습니다."));

        return ProductWishResponse.toDto(findProductWish);
    }

    public ProductWishResponse deleteProductWish(final Long userId, final Long productWishId) {
        ProductWish findProductWish = productWishRepository.findByIdAndUserId(productWishId, userId)
                .orElseThrow(() -> new ProductNotFountException("찜한 제품을 찾을 수 없습니다."));
        int deleteCount = productWishRepository.deleteProductWishByIdAndUserId(productWishId, userId);

        if (deleteCount == 0) {
            throw new ProductNotFountException("찜한 제품을 찾을 수 없습니다.");
        }

        return ProductWishResponse.toDto(findProductWish);
    }
}
