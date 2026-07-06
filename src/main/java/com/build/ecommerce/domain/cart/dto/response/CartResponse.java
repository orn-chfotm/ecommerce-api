package com.build.ecommerce.domain.cart.dto.response;

import com.build.ecommerce.domain.cart.entity.Cart;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantValueResponse;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CartResponse(
        Long cartId,
        Long productId,
        String productName,
        BigDecimal price,
        int cartQuantity,
        Integer stockQuantity,
        @Schema(description = "선택한 옵션 조합(SKU) PK, 옵션 미등록 상품이면 null")
        Long productOptionVariantId,
        @Schema(description = "선택한 옵션 조합 SKU, 옵션 미등록 상품이면 null")
        String sku,
        @Schema(description = "선택한 옵션 값 목록, 옵션 미등록 상품이면 null")
        List<ProductOptionVariantValueResponse> selectedOptions
) {
    /**
     * 목록 조회용 - selectedOptions는 Service에서 variant id들을 모아 배치 조회한 결과를 주입받는다.
     * (productOptionVariantValues를 엔티티 통해 직접 lazy loading 하면 목록 건수만큼 N+1이 발생하므로 금지)
     */
    public static CartResponse toDto(Cart cart, List<ProductOptionVariantValueResponse> selectedOptions) {
        ProductOptionVariant variant = cart.getProductOptionVariant();

        return CartResponse.builder()
                .cartId(cart.getId())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getName())
                .price(cart.getProduct().getPrice())
                .cartQuantity(cart.getQuantity())
                .stockQuantity(variant == null ? cart.getProduct().getStockQuantity() : variant.getStockQuantity())
                .productOptionVariantId(variant == null ? null : variant.getId())
                .sku(variant == null ? null : variant.getSku())
                .selectedOptions(variant == null ? null : selectedOptions)
                .build();
    }

    /**
     * 단건 조회/응답용 - 대상이 한 건뿐이라 productOptionVariantValues를 바로 lazy loading 해도 무방하다.
     */
    public static CartResponse toDto(Cart cart) {
        ProductOptionVariant variant = cart.getProductOptionVariant();
        List<ProductOptionVariantValueResponse> selectedOptions = variant == null
                ? null
                : variant.getProductOptionVariantValues().stream()
                        .map(ProductOptionVariantValueResponse::toDto)
                        .toList();

        return toDto(cart, selectedOptions);
    }
}
