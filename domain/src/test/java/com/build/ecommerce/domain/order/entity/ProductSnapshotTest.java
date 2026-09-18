package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSnapshotTest {

    @Test
    @DisplayName("Product의 현재 값을 그대로 복사한 스냅샷을 생성한다")
    void from_copiesProductFields() {
        Product product = Product.builder()
                .category(ProductCategoryType.FASHION)
                .name("장갑")
                .description("따뜻한 장갑")
                .price(BigDecimal.valueOf(10000))
                .stockQuantity(10)
                .minOrderQuantity(1)
                .active(true)
                .build();

        ProductSnapshot snapshot = ProductSnapshot.from(product);

        assertThat(snapshot.getCategory()).isEqualTo(ProductCategoryType.FASHION);
        assertThat(snapshot.getName()).isEqualTo("장갑");
        assertThat(snapshot.getDescription()).isEqualTo("따뜻한 장갑");
        assertThat(snapshot.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }
}
