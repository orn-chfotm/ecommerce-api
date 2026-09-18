package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.domain.product.dto.request.ProductWishRequest;
import com.build.ecommerce.domain.product.dto.response.FileDetailResponse;
import com.build.ecommerce.domain.product.dto.response.ProductWishResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.code.UserExceptionCode;
import com.build.ecommerce.domain.file.entity.FileMaster;
import com.build.ecommerce.domain.file.repository.FileMasterRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.product.repository.ProductWishRepository;
import com.build.ecommerce.domain.user.repository.UserRepository;
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
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        // 추가구성상품은 단독 노출 대상이 아니므로 찜 대상이 될 수 없다.
        if (findProduct.isAddOn()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_PRODUCT_NOT_ALLOWED_IN_WISH);
        }

        // 삭제되었거나 노출 조건을 만족하지 않는 상품은 새로 찜할 수 없다.
        if (!findProduct.isVisibleToUser()) {
            throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_DISPLAYED);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserExceptionCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_WISH_NOT_FOUND));

        return ProductWishResponse.toDto(findProductWish);
    }

    public ProductWishResponse deleteProductWish(final Long userId, final Long productWishId) {
        ProductWish findProductWish = productWishRepository.findByIdAndUserId(productWishId, userId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_WISH_NOT_FOUND));
        productWishRepository.delete(findProductWish);
        return ProductWishResponse.toDto(findProductWish);
    }
}
