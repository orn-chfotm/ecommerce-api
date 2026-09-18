package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductOptionVariantSnapshotTest {

    @Test
    @DisplayName("ProductOptionVariant의 현재 값을 그대로 복사한 스냅샷을 생성한다")
    void from_copiesVariantFields() {
        ProductOptionVariant variant = ProductOptionVariant.builder()
                .product(null)
                .sku("SKU-M")
                .stockQuantity(10)
                .priceDelta(BigDecimal.valueOf(500))
                .active(true)
                .maxPurchaseQuantity(null)
                .build();

        ProductOptionVariantSnapshot snapshot = ProductOptionVariantSnapshot.from(variant);

        assertThat(snapshot.getSku()).isEqualTo("SKU-M");
        assertThat(snapshot.getPriceDelta()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }
}
