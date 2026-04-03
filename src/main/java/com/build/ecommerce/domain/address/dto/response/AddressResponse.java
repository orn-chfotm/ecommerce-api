package com.build.ecommerce.domain.address.dto.response;

import java.util.List;

public record AddressResponse(
        List<AddressInfoResponse> addressList
) {

    private static class AddressResponseBuilder {
        private List<AddressInfoResponse> addressList;

        private AddressResponseBuilder addressList(List<AddressInfoResponse> addressList) {
            this.addressList = addressList;
            return this;
        }

        private AddressResponse build() {
            return new AddressResponse(addressList);
        }
    }

    public static AddressResponseBuilder builder() {
        return new AddressResponseBuilder();
    }

    public static AddressResponse toDto(List<AddressInfoResponse> addressInfoResponse) {
        return AddressResponse.builder()
                .addressList(addressInfoResponse)
                .build();
    }
}
