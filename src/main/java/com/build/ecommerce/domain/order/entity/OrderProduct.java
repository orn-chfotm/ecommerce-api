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

    @Column(nullable = false)
    @Comment(value = "order total price")
    private BigDecimal totalPrice;

    @Column(nullable = false)
    @Comment(value = "order product quantity")
    private int quantity;

    @Builder
    public OrderProduct(Product product, ProductOptionVariant productOptionVariant, BigDecimal totalPrice, int quantity) {
        this.product = product;
        this.productOptionVariant = productOptionVariant;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
