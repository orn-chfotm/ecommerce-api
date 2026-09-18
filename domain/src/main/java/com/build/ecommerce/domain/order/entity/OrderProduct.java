package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "Order Product relation table")
public class OrderProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_PRODUCT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    @Comment(value = "order table fk")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Comment(value = "product table fk")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_VARIANT_ID")
    @Comment(value = "주문 시점 옵션 조합(SKU) FK, 옵션 미등록 상품 주문이면 null")
    private ProductOptionVariant productOptionVariant;

    /*
     * 추가구성상품 라인이면 본품 라인을 가리키고, 본품 라인이면 null이다.
     * 자기참조에는 cascade를 걸지 않는다 - Order.orderProducts의 cascade=PERSIST로만 영속화한다.
     * Order.orderProducts는 컬렉션에 담긴 순서대로 insert되고 PK 전략이 IDENTITY이므로,
     * 반드시 본품 라인을 자식(추가구성상품) 라인보다 먼저 addOrderProduct 해야
     * 본품 라인의 PK가 먼저 채번되어 이 FK가 안전하게 채워진다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORDER_PRODUCT_ID")
    @Comment(value = "추가구성상품 라인이면 본품 라인 FK, 본품 라인이면 null")
    private OrderProduct parentOrderProduct;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "category", column = @Column(name = "SNAPSHOT_PRODUCT_CATEGORY")),
            @AttributeOverride(name = "name", column = @Column(name = "SNAPSHOT_PRODUCT_NAME")),
            @AttributeOverride(name = "description", column = @Column(name = "SNAPSHOT_PRODUCT_DESCRIPTION")),
            @AttributeOverride(name = "price", column = @Column(name = "SNAPSHOT_PRODUCT_PRICE"))
    })
    @Comment(value = "주문 시점 제품 스냅샷")
    private ProductSnapshot productSnapshot;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "sku", column = @Column(name = "SNAPSHOT_OPTION_SKU")),
            @AttributeOverride(name = "priceDelta", column = @Column(name = "SNAPSHOT_OPTION_PRICE_DELTA"))
    })
    @Comment(value = "주문 시점 옵션 조합 스냅샷")
    private ProductOptionVariantSnapshot productOptionVariantSnapshot;

    @Column(nullable = false)
    @Comment(value = "order total price")
    private BigDecimal totalPrice;

    @Column(nullable = false)
    @Comment(value = "order product quantity")
    private int quantity;

    @Builder
    public OrderProduct(Product product, ProductOptionVariant productOptionVariant, ProductSnapshot productSnapshot, ProductOptionVariantSnapshot productOptionVariantSnapshot, BigDecimal totalPrice, int quantity, OrderProduct parentOrderProduct) {
        this.product = product;
        this.productOptionVariant = productOptionVariant;
        this.productSnapshot = productSnapshot;
        this.productOptionVariantSnapshot = productOptionVariantSnapshot;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.parentOrderProduct = parentOrderProduct;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
