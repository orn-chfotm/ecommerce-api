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
@Table(name = "PRODUCT_OPTION_VARIANT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "상품 옵션 조합(SKU) 테이블", on = "TABLE")
public class ProductOptionVariant extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_OPTION_VARIANT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 FK")
    private Product product;

    @Column(length = 100)
    @Comment("판매자 관리용 SKU 코드 (선택, 로직에서는 PK를 기준으로 사용)")
    private String sku;

    @Column(nullable = false)
    @Comment("옵션 조합 재고 수량")
    private int stockQuantity;

    @Column(nullable = false)
    @Comment("옵션 조합 추가 금액, default 0")
    private BigDecimal priceDelta;

    @Column(nullable = false)
    @Comment("판매 활성화 여부, default true")
    private boolean active;

    @Comment("옵션 조합별 최대 구매 수량 (null 은 미지정)")
    private Integer maxPurchaseQuantity;

    @OneToMany(mappedBy = "productOptionVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("조합을 구성하는 옵션 값 매핑")
    private List<ProductOptionVariantValue> productOptionVariantValues = new ArrayList<>();

    @Builder
    public ProductOptionVariant(Product product, String sku, int stockQuantity, BigDecimal priceDelta, boolean active, Integer maxPurchaseQuantity) {
        this.product = product;
        this.sku = sku;
        this.stockQuantity = stockQuantity;
        this.priceDelta = priceDelta;
        this.active = active;
        this.maxPurchaseQuantity = maxPurchaseQuantity;
    }

    public void addProductOptionVariantValue(ProductOptionVariantValue productOptionVariantValue) {
        this.productOptionVariantValues.add(productOptionVariantValue);
        productOptionVariantValue.setProductOptionVariant(this);
    }

    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new ProductNotEnoughStockException();
        }
        this.stockQuantity = restStock;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void changeStock(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
        this.stockQuantity = stockQuantity;
    }

    public void deactivate() {
        this.active = false;
    }
}
