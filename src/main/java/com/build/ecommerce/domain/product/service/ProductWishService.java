package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.domain.product.dto.request.ProductWishRequest;
import com.build.ecommerce.domain.product.dto.response.FileDetailResponse;
import com.build.ecommerce.domain.product.dto.response.ProductWishResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.product.exception.ProductWishNotFoundException;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.infra.file.entity.FileMaster;
import com.build.ecommerce.infra.persistence.file.FileMasterRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.infra.persistence.product.ProductWishRepository;
import com.build.ecommerce.infra.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductWishService {

    private final ProductWishRepository productWishRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileMasterRepository fileMasterRepository;

    public ProductWishResponse registerProductWish(Long userId, ProductWishRequest request) {
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(ProductNotFoundException::new);
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ProductWish saveProductWish = ProductWish.builder()
                .user(findUser)
                .product(findProduct)
                .build();

        return ProductWishResponse.toDto(productWishRepository.save(saveProductWish));
    }

    @Transactional(readOnly = true)
    public List<ProductWishResponse> selectProductWishList(final Long userId) {
        List<ProductWish> wishList = productWishRepository.findByUserId(userId);

        List<Long> fileMasterIds = wishList.stream()
                .map(wish -> wish.getProduct().getFileMaster())
                .filter(fm -> fm != null)
                .map(FileMaster::getId)
                .distinct()
                .toList();

        Map<Long, List<FileDetailResponse>> filesByFileMasterId = fileMasterIds.isEmpty()
                ? Map.of()
                : fileMasterRepository.findAllWithDetailsByIdIn(fileMasterIds).stream()
                        .collect(Collectors.toMap(FileMaster::getId, fm -> fm.getFileDetailList().stream()
                                .map(FileDetailResponse::toDto)
                                .toList()));

        return wishList.stream()
                .map(wish -> {
                    FileMaster fileMaster = wish.getProduct().getFileMaster();
                    List<FileDetailResponse> files = fileMaster == null ? null : filesByFileMasterId.get(fileMaster.getId());
                    return ProductWishResponse.toDto(wish, files);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductWishResponse selectProductWishDetail(final Long userId, final Long productWishId) {
        ProductWish findProductWish = productWishRepository.findByIdAndUserId(productWishId, userId)
                .orElseThrow(ProductWishNotFoundException::new);

        return ProductWishResponse.toDto(findProductWish);
    }

    public ProductWishResponse deleteProductWish(final Long userId, final Long productWishId) {
        ProductWish findProductWish = productWishRepository.findByIdAndUserId(productWishId, userId)
                .orElseThrow(ProductWishNotFoundException::new);
        productWishRepository.delete(findProductWish);
        return ProductWishResponse.toDto(findProductWish);
    }
}
