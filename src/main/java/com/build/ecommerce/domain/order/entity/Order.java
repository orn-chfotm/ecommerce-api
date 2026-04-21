package com.build.ecommerce.domain.order.entity;

import com.build.ecommerce.common.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.order.enums.OrderStatusType;
import com.build.ecommerce.domain.order.exception.OrderStatusException;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "Order infomation table", on = "TABLE")
public class Order extends BaseTimeEntity {

    @Comment(value = "order table pk")
    @Column(name = "ORDER_ID")
    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("주문 상태")
    private OrderStatusType status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Embedded
    @Column(nullable = false)
    @Comment("주문 시 배송지 정보")
    private AddressInfo addressInfo;

    @Builder
    public Order(OrderStatusType status, User user, AddressInfo addressInfo) {
        this.status = status;
        this.user = user;
        this.addressInfo = addressInfo;
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void cancel() {
        if (isCancelable()) {
            this.status = OrderStatusType.CANCEL;
        } else {
            throw new OrderStatusException();
        }
    }

    private boolean isCancelable() {
        return OrderStatusType.getCancelable().stream()
                .anyMatch(s -> s.equals(this.status));
    }
}
