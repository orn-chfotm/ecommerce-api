package com.build.ecommerce.domain.order.dto.response;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.entity.ProductOptionVariantSnapshot;
import com.build.ecommerce.domain.order.entity.ProductSnapshot;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantValueResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OrderedProductDetailResponse(
        @Schema(description = "제품 카테고리")
        ProductCategoryType category,
        @Schema(description = "제품명")
        String name,
        @Schema(description = "제품 설명")
        String description,
        @Schema(description = "제품 가격")
        BigDecimal price,
        @Schema(description = "주문한 옵션 조합 SKU (옵션 미등록 상품이면 null)")
        String sku,
        @Schema(description = "주문한 옵션 값 목록 (옵션 미등록 상품이면 null)")
        List<ProductOptionVariantValueResponse> selectedOptions
) {
    /**
     * 목록 조회용 - selectedOptions는 Service에서 variant id들을 모아 배치 조회한 결과를 주입받는다.
     * (productOptionVariantValues를 엔티티 통해 직접 lazy loading 하면 목록 건수만큼 N+1이 발생하므로 금지)
     */
    public static OrderedProductDetailResponse toDto(OrderProduct orderProduct, List<ProductOptionVariantValueResponse> selectedOptions) {
        Product product = orderProduct.getProduct();
        ProductOptionVariant variant = orderProduct.getProductOptionVariant();
        ProductSnapshot productSnapshot = orderProduct.getProductSnapshot();
        ProductOptionVariantSnapshot variantSnapshot = orderProduct.getProductOptionVariantSnapshot();
        boolean hasProductSnapshot = hasProductSnapshot(productSnapshot);
        boolean hasVariantSnapshot = hasVariantSnapshot(variantSnapshot);

        return OrderedProductDetailResponse.builder()
                .category(hasProductSnapshot ? productSnapshot.getCategory() : product.getCategory())
                .name(hasProductSnapshot ? productSnapshot.getName() : product.getName())
                .description(hasProductSnapshot ? productSnapshot.getDescription() : product.getDescription())
                .price(hasProductSnapshot ? productSnapshot.getPrice() : product.getPrice())
                .sku(variant == null ? null : (hasVariantSnapshot ? variantSnapshot.getSku() : variant.getSku()))
                .selectedOptions(variant == null ? null : selectedOptions)
                .build();
    }

    /**
     * 단건 상세 조회용 - 주문 한 건만 대상이라 productOptionVariantValues를 바로 lazy loading 해도
     * N+1 영향이 크지 않아 엔티티에서 직접 조합한다.
     */
    public static OrderedProductDetailResponse toDetailDto(OrderProduct orderProduct) {
        Product product = orderProduct.getProduct();
        ProductOptionVariant variant = orderProduct.getProductOptionVariant();
        ProductSnapshot productSnapshot = orderProduct.getProductSnapshot();
        ProductOptionVariantSnapshot variantSnapshot = orderProduct.getProductOptionVariantSnapshot();
        boolean hasProductSnapshot = hasProductSnapshot(productSnapshot);
        boolean hasVariantSnapshot = hasVariantSnapshot(variantSnapshot);

        return OrderedProductDetailResponse.builder()
                .category(hasProductSnapshot ? productSnapshot.getCategory() : product.getCategory())
                .name(hasProductSnapshot ? productSnapshot.getName() : product.getName())
                .description(hasProductSnapshot ? productSnapshot.getDescription() : product.getDescription())
                .price(hasProductSnapshot ? productSnapshot.getPrice() : product.getPrice())
                .sku(variant == null ? null : (hasVariantSnapshot ? variantSnapshot.getSku() : variant.getSku()))
                .selectedOptions(variant == null ? null : variant.getProductOptionVariantValues().stream()
                        .map(ProductOptionVariantValueResponse::toDto)
                        .toList())
                .build();
    }

    /**
     * @Embedded 필드가 전부 null인 레거시 행에서 Hibernate가 참조 자체를 null로 주는지,
     * "필드가 전부 null인 빈 인스턴스"를 주는지는 신뢰할 수 없어 두 경우 모두 안전하게 처리한다.
     * category는 Product 엔티티에서 not-null이 보장되는 필드라 스냅샷 존재 판단 기준으로 쓴다.
     */
    private static boolean hasProductSnapshot(ProductSnapshot snapshot) {
        return snapshot != null && snapshot.getCategory() != null;
    }

    /**
     * priceDelta는 ProductOptionVariant 엔티티에서 not-null이 보장되는 필드라
     * 스냅샷 존재 판단 기준으로 쓴다.
     */
    private static boolean hasVariantSnapshot(ProductOptionVariantSnapshot snapshot) {
        return snapshot != null && snapshot.getPriceDelta() != null;
    }
}
