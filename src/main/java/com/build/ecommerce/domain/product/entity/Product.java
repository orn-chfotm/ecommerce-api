package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;


@Entity
@Table(name = "PRODUCTS")
@Comment(value = "product information table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Comment("제품 카테고리, not null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Comment("제품 명, not null")
    @Column(nullable = false)
    private String name;

    @Comment("제품 설명")
    private String description;

    @Comment("제품 가격, not null")
    @Column(nullable = false)
    private BigDecimal price;

    @Comment("제품 수량, (null 은 미지정 / 0 은 재고 없음 의미)")
    private Integer stockQuantity;

    @Comment("제품 최소 주문 수량, default 0")
    private int minOrderQuantity;

    @Comment("제품 노출 여부, default false")
    private boolean active;

    @Builder
    public Product(Category category, String name, String description, BigDecimal price, Integer stockQuantity, int minOrderQuantity, boolean active) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minOrderQuantity = minOrderQuantity;
        this.active = active;
    }
}
