package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Comment(value = "Order infomation table" ,on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

    @Comment(value = "order table pk")
    @Column(name = "ORDER_ID")
    @Id @GeneratedValue
    private Long id;

    @Comment(value = "order number, not pk")
    @Column(name = "ORDER_NUMBER", nullable = false, unique = true)
    @GeneratedValue
    private Long orderNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Embedded
    @Comment("주문 시 배송지 정보")
    private Address address;

    @Builder
    public Order(Status status, User user) {
        this.status = status;
        this.user = user;
    }

    public void addOrder(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOder(this);
    }
}
