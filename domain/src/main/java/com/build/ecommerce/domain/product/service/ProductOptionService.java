package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.product.dto.request.ProductOptionGroupRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantStockRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.response.ProductOptionResponse;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantResponse;
import com.build.ecommerce.domain.product.dto.response.ProductOptionsResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOption;
import com.build.ecommerce.domain.product.entity.ProductOptionValue;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.product.repository.ProductOptionRepository;
import com.build.ecommerce.domain.product.repository.ProductOptionVariantRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionVariantRepository productOptionVariantRepository;

    public ProductOptionsResponse registerProductOptions(final Long productId, ProductOptionRegisterRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        // 추가구성상품은 옵션 없는 단일 SKU로만 취급한다.
        if (product.isAddOn()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_PRODUCT_OPTION_NOT_ALLOWED);
        }

        // 삭제된 상품에는 옵션을 등록할 수 없다.
        // 단, 미노출(active=false) 상품은 "비노출로 준비 → 옵션 세팅 → 이후 노출" 흐름을 위해 허용한다.
        if (product.isDeleted()) {
            throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_DISPLAYED);
        }

        if (product.isHasOptions()) {
            throw new BusinessException(ProductExceptionCode.PRODUCT_OPTION_ALREADY_REGISTERED);
        }

        Map<String, ProductOption> optionsByName = new HashMap<>();
        Map<String, ProductOptionValue> valuesByOptionAndValue = new HashMap<>();

        for (ProductOptionGroupRequest groupRequest : request.options()) {
            ProductOption productOption = ProductOption.builder()
                    .product(product)
                    .name(groupRequest.name())
                    .sortOrder(groupRequest.sortOrder())
                    .build();

            for (int i = 0; i < groupRequest.values().size(); i++) {
                String value = groupRequest.values().get(i);
                ProductOptionValue productOptionValue = ProductOptionValue.builder()
                        .value(value)
                        .sortOrder(i)
                        .build();
                productOption.addProductOptionValue(productOptionValue);
                valuesByOptionAndValue.put(valueKey(groupRequest.name(), value), productOptionValue);
            }

            productOptionRepository.save(productOption);
            optionsByName.put(groupRequest.name(), productOption);
        }

        for (ProductOptionVariantRequest variantRequest : request.variants()) {
            ProductOptionVariant variant = ProductOptionVariant.builder()
                    .product(product)
                    .sku(variantRequest.sku())
                    .stockQuantity(variantRequest.stockQuantity())
                    .priceDelta(variantRequest.priceDelta() == null ? BigDecimal.ZERO : variantRequest.priceDelta())
                    .active(true)
                    .maxPurchaseQuantity(variantRequest.maxPurchaseQuantity())
                    .build();

            for (ProductOptionVariantValueRequest valueRequest : variantRequest.optionValues()) {
                ProductOption productOption = optionsByName.get(valueRequest.optionName());
                if (productOption == null) {
                    throw new InvalidInputException("등록되지 않은 옵션 명입니다: " + valueRequest.optionName());
                }

                ProductOptionValue productOptionValue = valuesByOptionAndValue.get(valueKey(valueRequest.optionName(), valueRequest.value()));
                if (productOptionValue == null) {
                    throw new InvalidInputException("등록되지 않은 옵션 값입니다: " + valueRequest.optionName() + "=" + valueRequest.value());
                }

                variant.addProductOptionVariantValue(ProductOptionVariantValue.builder()
                        .productOption(productOption)
                        .productOptionValue(productOptionValue)
                        .build());
            }

            productOptionVariantRepository.save(variant);
        }

        product.markOptionsRegistered();

        return getProductOptions(productId);
    }

    @Transactional(readOnly = true)
    public ProductOptionsResponse getProductOptions(final Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        List<ProductOptionResponse> options = productOptionRepository.findAllWithValuesByProductId(productId).stream()
                .map(ProductOptionResponse::toDto)
                .toList();

        List<ProductOptionVariantResponse> variants = productOptionVariantRepository.findAllWithValuesByProductId(productId).stream()
                .map(ProductOptionVariantResponse::toDto)
                .toList();

        return ProductOptionsResponse.toDto(product, options, variants);
    }

    public ProductOptionVariantResponse updateVariantStock(final Long productId, final Long variantId, ProductOptionVariantStockRequest request) {
        ProductOptionVariant variant = productOptionVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_OPTION_VARIANT_NOT_FOUND));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new NotFoundException(ProductExceptionCode.PRODUCT_OPTION_VARIANT_NOT_FOUND);
        }

        variant.changeStock(request.stockQuantity());

        return ProductOptionVariantResponse.toDto(variant);
    }

    private String valueKey(String optionName, String value) {
        return optionName + "::" + value;
    }
}
