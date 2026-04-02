package com.build.ecommerce.domain.address.entity;

import com.build.ecommerce.core.util.BaseEntity;
import com.build.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Embeddable
@NoArgsConstructor
@Getter
public class Address extends BaseEntity {

    @Comment(value = "도로명 주소 or 지번 주소")
    @Column(nullable = false)
    private String address;

    @Comment(value = "상세 주소")
    @Column(nullable = false)
    private String extraAddress;

    @Comment(value = "우편 번호")
    @Column(nullable = false)
    private String zipCode;

    @Builder
    public Address(String address, String extraAddress, String zipCode) {
        this.address = address;
        this.extraAddress = extraAddress;
        this.zipCode = zipCode;
    }
}
