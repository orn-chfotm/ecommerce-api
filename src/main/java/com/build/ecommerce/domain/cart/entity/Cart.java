package com.build.ecommerce.domain.cart.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "CARTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "Cart table", on = "TABLE")
public class Cart extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    @Comment("PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    @Comment("사용자 FK")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 FK")
    private Product product;

    @Column(nullable = false)
    @Comment("장바구니 수량")
    private int quantity;

    @Builder
    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
