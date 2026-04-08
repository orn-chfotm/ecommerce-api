package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.product.exception.ProductNotEnoughStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "PRODUCTS")
@Comment(value = "product information table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Comment("제품 카테고리, not null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategoryType category;

    @Comment("제품 명, not null")
    @Column(nullable = false)
    private String name;

    @Comment("제품 설명")
    private String description;

    @Column(nullable = false)
    @Comment("제품 가격, not null")
    private BigDecimal price;

    @Comment("제품 수량, (null 은 미지정 / 0 은 재고 없음 의미)")
    private Integer stockQuantity;

    @Comment("제품 최소 주문 수량, default 1")
    private int minOrderQuantity = 1;

    @Comment("제품 노출 여부, default false")
    private boolean active;

    @OneToMany(mappedBy = "product")
    @Comment("찜 리스트")
    private List<ProductWish> productWish = new ArrayList<>();

    @Builder
    public Product(ProductCategoryType category, String name, String description, BigDecimal price, Integer stockQuantity, int minOrderQuantity, boolean active) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minOrderQuantity = minOrderQuantity;
        this.active = active;
    }

    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new ProductNotEnoughStockException();
        }
        this.stockQuantity = restStock;
    }
}
