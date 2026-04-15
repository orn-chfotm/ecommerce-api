package com.build.ecommerce.domain.address.dto.request;

import com.build.ecommerce.domain.address.dto.jackson.AddressTypeDeserializer;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotNull(message = "주소지 형식을 선택해주세요.")
        @Schema(description = "주소 타입 (예: REGION_ADDR, ROAD_ADDR)")
        @JsonDeserialize(using = AddressTypeDeserializer.class)
        AddressType addressType,
        @NotNull(message = "주소지를 입력해야 합니다.")
        @Schema(description = "주소지")
        String address,
        @NotNull(message = "상세 주소지를 입력해야 합니다.")
        @Schema(description = "상세 주소")
        String extraAddress,
        @NotNull(message = "우편 번호를 입력해야합니다.")
        @Schema(description = "우편 번호")
        String zipCode
) {
        public static AddressInfo toEntity(AddressRequest request) {
                return AddressInfo.builder()
                        .addressType(request.addressType)
                        .address(request.address)
                        .extraAddress(request.extraAddress)
                        .zipCode(request.zipCode)
                        .build();
        }
}
