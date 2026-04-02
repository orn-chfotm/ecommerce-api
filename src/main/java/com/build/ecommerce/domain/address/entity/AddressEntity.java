package com.build.ecommerce.domain.address.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "ADDRESS")
@Comment(value = "Delivery Address Table, Join Users Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddressEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private Long id;

    @Embedded
    private Address address;

    @Builder
    public AddressEntity(Address address) {
        this.address = address;
    }
}
