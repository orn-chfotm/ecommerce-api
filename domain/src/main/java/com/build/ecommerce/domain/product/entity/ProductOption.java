package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_OPTION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "상품 옵션 명 테이블 (예: 색상, 사이즈)", on = "TABLE")
public class ProductOption extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_OPTION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 FK")
    private Product product;

    @Column(nullable = false, length = 100)
    @Comment("옵션 명 명, not null (예: 색상, 사이즈)")
    private String name;

    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("정렬 순서")
    private int sortOrder;

    @OneToMany(mappedBy = "productOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Comment("옵션 값 리스트")
    private List<ProductOptionValue> productOptionValues = new ArrayList<>();

    @Builder
    public ProductOption(Product product, String name, int sortOrder) {
        this.product = product;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public void addProductOptionValue(ProductOptionValue productOptionValue) {
        this.productOptionValues.add(productOptionValue);
        productOptionValue.setProductOption(this);
    }
}
