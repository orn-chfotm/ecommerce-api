package com.build.ecommerce.domain.address.entity;

import com.build.ecommerce.domain.address.enums.AddressType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddressInfo {

    @Comment(value = "주소 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType addressType;

    @Comment(value = "전체 지번 주소 또는 전체 도로명 주소")
    @Column(nullable = false)
    private String address;

    @Comment(value = "상세 주소")
    @Column(nullable = false)
    private String extraAddress;

    @Comment(value = "우편 번호")
    @Column(nullable = false)
    private String zipCode;

    @Builder
    public AddressInfo(AddressType addressType, String address, String extraAddress, String zipCode) {
        this.addressType = addressType;
        this.address = address;
        this.extraAddress = extraAddress;
        this.zipCode = zipCode;
    }
}
