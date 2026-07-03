package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "PRODUCT_OPTION_VARIANT_VALUE",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_variant_option_axis", columnNames = {"PRODUCT_OPTION_VARIANT_ID", "PRODUCT_OPTION_ID"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "옵션 조합(Variant)과 옵션 값의 매핑 테이블", on = "TABLE")
public class ProductOptionVariantValue extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_OPTION_VARIANT_VALUE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_VARIANT_ID", nullable = false)
    @Comment("옵션 조합(Variant) FK")
    private ProductOptionVariant productOptionVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_ID", nullable = false)
    @Comment("상품 옵션 명 FK (한 조합에 같은 명이 중복되지 않도록 보장)")
    private ProductOption productOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_VALUE_ID", nullable = false)
    @Comment("상품 옵션 값 FK")
    private ProductOptionValue productOptionValue;

    @Builder
    public ProductOptionVariantValue(ProductOption productOption, ProductOptionValue productOptionValue) {
        this.productOption = productOption;
        this.productOptionValue = productOptionValue;
    }

    void setProductOptionVariant(ProductOptionVariant productOptionVariant) {
        this.productOptionVariant = productOptionVariant;
    }
}
