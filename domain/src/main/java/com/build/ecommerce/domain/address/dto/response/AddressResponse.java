package com.build.ecommerce.domain.address.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressResponse(
        List<AddressInfoResponse> addressList
) {
    public static AddressResponse toDto(List<AddressInfoResponse> addressInfoResponse) {
        return AddressResponse.builder()
                .addressList(addressInfoResponse)
                .build();
    }
}
