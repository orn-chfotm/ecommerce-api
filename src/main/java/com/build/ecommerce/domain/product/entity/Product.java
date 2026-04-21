package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.common.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductStatusType;
import com.build.ecommerce.domain.product.exception.ProductNotEnoughStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "PRODUCTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment(value = "product information table", on = "TABLE")
@Getter
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("제품 카테고리, not null")
    private ProductCategoryType category;

    @Column(nullable = false, length = 200)
    @Comment("제품 명, not null")
    private String name;

    @Column(length = 2000)
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

    @Comment("제품 상태 값")
    private ProductStatusType status;

    @Comment("제품 상태 값")
    private LocalDateTime delAt;

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

    public void changeStatus(ProductStatusType statusType) {
        this.status = statusType;
    }

    public void markDelete() {
        this.status = ProductStatusType.DELETED;
        this.delAt = LocalDateTime.now();
    }
}
