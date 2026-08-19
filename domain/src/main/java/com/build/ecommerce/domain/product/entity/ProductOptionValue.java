package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "PRODUCT_OPTION_VALUE",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_option_value_sort_order", columnNames = {"PRODUCT_OPTION_ID", "SORT_ORDER"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "상품 옵션 값 테이블", on = "TABLE")
public class ProductOptionValue extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_OPTION_VALUE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_ID", nullable = false)
    @Comment("상품 옵션 명 FK")
    private ProductOption productOption;

    @Column(name = "OPTION_VALUE", nullable = false, length = 100)
    @Comment("옵션 값, not null (예: 블랙, M)")
    private String value;

    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("정렬 순서")
    private int sortOrder;

    @Builder
    public ProductOptionValue(String value, int sortOrder) {
        this.value = value;
        this.sortOrder = sortOrder;
    }

    void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }
}
