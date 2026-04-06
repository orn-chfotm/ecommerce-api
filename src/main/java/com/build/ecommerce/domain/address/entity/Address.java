package com.build.ecommerce.domain.address.entity;

import com.build.ecommerce.core.util.entity.BaseTimeEntity;
import com.build.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "ADDRESS")
@Comment(value = "Delivery AddressInfo Table, Join Users Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private AddressInfo addressInfo;

    @Builder
    public Address(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }

    public void changeUser(User user) {
        this.user = user;
    }
}
