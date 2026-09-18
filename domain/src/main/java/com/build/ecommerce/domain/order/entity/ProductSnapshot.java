package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

/**
 * 주문 시점 제품 정보 스냅샷. 관리자가 이후 상품 정보를 수정해도 과거 주문 조회 결과가
 * 바뀌지 않도록 하기 위함이다. 스냅샷 컬럼은 기존 주문 데이터와의 하위 호환을 위해
 * 항상 nullable이어야 하므로 필드에 nullable=false를 두지 않는다.
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductSnapshot {

    @Comment(value = "주문 시점 제품 카테고리 스냅샷")
    @Enumerated(EnumType.STRING)
    private ProductCategoryType category;

    @Comment(value = "주문 시점 제품명 스냅샷")
    private String name;

    @Comment(value = "주문 시점 제품 설명 스냅샷")
    private String description;

    @Comment(value = "주문 시점 제품 가격 스냅샷")
    private BigDecimal price;

    @Builder
    public ProductSnapshot(ProductCategoryType category, String name, String description, BigDecimal price) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static ProductSnapshot from(Product product) {
        return ProductSnapshot.builder()
                .category(product.getCategory())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
