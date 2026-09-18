package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

/**
 * 주문 시점 옵션 조합(SKU) 스냅샷.
 * 옵션명/값 자체(예: "색상: 블랙")를 수정·삭제하는 API가 현재 없어 스냅샷 대상에서 제외했다.
 * 향후 그런 API가 추가되면 재검토가 필요하다.
 * 스냅샷 컬럼은 기존 주문 데이터와의 하위 호환을 위해 항상 nullable이어야 하므로
 * 필드에 nullable=false를 두지 않는다.
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOptionVariantSnapshot {

    @Comment(value = "주문 시점 옵션 조합 SKU 스냅샷")
    private String sku;

    @Comment(value = "주문 시점 옵션 조합 추가 금액 스냅샷")
    private BigDecimal priceDelta;

    @Builder
    public ProductOptionVariantSnapshot(String sku, BigDecimal priceDelta) {
        this.sku = sku;
        this.priceDelta = priceDelta;
    }

    public static ProductOptionVariantSnapshot from(ProductOptionVariant variant) {
        return ProductOptionVariantSnapshot.builder()
                .sku(variant.getSku())
                .priceDelta(variant.getPriceDelta())
                .build();
    }
}
