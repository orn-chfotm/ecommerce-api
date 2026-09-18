package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "PRODUCT_ADD_ON",
        uniqueConstraints = @UniqueConstraint(name = "UK_PRODUCT_ADD_ON", columnNames = {"PRODUCT_ID", "ADD_ON_PRODUCT_ID"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "본품 - 추가구성상품 매핑 테이블", on = "TABLE")
public class ProductAddOn extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ADD_ON_ID")
    @Comment("PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Comment("본품 상품 FK")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADD_ON_PRODUCT_ID", nullable = false)
    @Comment("추가구성상품 FK")
    private Product addOnProduct;

    @Column(nullable = false)
    @Comment("노출 정렬 순서")
    private int sortOrder;

    @Builder
    public ProductAddOn(Product product, Product addOnProduct, int sortOrder) {
        this.product = product;
        this.addOnProduct = addOnProduct;
        this.sortOrder = sortOrder;
    }
}
