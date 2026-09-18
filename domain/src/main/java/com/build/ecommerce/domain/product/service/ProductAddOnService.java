package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.product.dto.request.ProductAddOnRequest;
import com.build.ecommerce.domain.product.dto.response.ProductAddOnResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductAddOn;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.product.repository.ProductAddOnRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductAddOnService {

    private final ProductRepository productRepository;
    private final ProductAddOnRepository productAddOnRepository;

    public ProductAddOnResponse registerAddOn(final Long productId, ProductAddOnRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        // 추가구성상품에 다시 추가구성상품을 붙이지 않도록 1단계 깊이로 고정한다.
        if (product.isAddOn()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_PARENT_NOT_NORMAL_TYPE);
        }

        if (request.addOnProductId().equals(productId)) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_SELF_REFERENCE_NOT_ALLOWED);
        }

        Product addOnProduct = productRepository.findById(request.addOnProductId())
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.ADD_ON_PRODUCT_NOT_FOUND));

        if (!addOnProduct.isAddOn()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_TARGET_NOT_ADD_ON_TYPE);
        }

        // 추가구성상품은 옵션 없는 단일 SKU만 허용한다.
        if (addOnProduct.isHasOptions()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_TARGET_HAS_OPTIONS);
        }

        if (productAddOnRepository.existsByProductIdAndAddOnProductId(productId, addOnProduct.getId())) {
            throw new BusinessException(ProductExceptionCode.ADD_ON_ALREADY_REGISTERED);
        }

        // 등록은 항상 마지막 순서로 추가한다.
        int sortOrder = productAddOnRepository.findMaxSortOrderByProductId(productId)
                .map(maxSortOrder -> maxSortOrder + 1)
                .orElse(0);

        ProductAddOn productAddOn = ProductAddOn.builder()
                .product(product)
                .addOnProduct(addOnProduct)
                .sortOrder(sortOrder)
                .build();

        return ProductAddOnResponse.toDto(productAddOnRepository.save(productAddOn));
    }

    @Transactional(readOnly = true)
    public List<ProductAddOnResponse> getAddOns(final Long productId) {
        findProductById(productId);

        return toResponses(productId);
    }

    /**
     * 사용자 노출용 조회. 본품 자체가 추가구성상품이면 존재를 숨긴다.
     */
    @Transactional(readOnly = true)
    public List<ProductAddOnResponse> getAddOnsForUser(final Long productId) {
        Product product = findProductById(productId);

        if (product.isAddOn()) {
            throw new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND);
        }

        // 비노출/삭제된 추가구성상품은 사용자에게 선택지로 보여선 안 되므로 결과에서 제외한다.
        return productAddOnRepository.findAllByProductId(productId).stream()
                .filter(productAddOn -> productAddOn.getAddOnProduct().isVisibleToUser())
                .map(ProductAddOnResponse::toDto)
                .toList();
    }

    private Product findProductById(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));
    }

    private List<ProductAddOnResponse> toResponses(final Long productId) {
        return productAddOnRepository.findAllByProductId(productId).stream()
                .map(ProductAddOnResponse::toDto)
                .toList();
    }
}
