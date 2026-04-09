package com.build.ecommerce.domain.address.dto.response;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressInfoResponse(
        @Schema(description = "주소 타입")
        AddressType addressType,
        @Schema(description = "주소지")
        String address,
        @Schema(description = "상세 주소지")
        String extraAddress,
        @Schema(description = "우편 번호")
        String zipCode
) {
    public static AddressInfoResponse toDto(Address addressEntity) {
        AddressInfo addressInfo = addressEntity.getAddressInfo();
        return AddressInfoResponse.builder()
                .addressType(addressInfo.getAddressType())
                .address(addressInfo.getAddress())
                .extraAddress(addressInfo.getExtraAddress())
                .zipCode(addressInfo.getZipCode())
                .build();
    }
}
