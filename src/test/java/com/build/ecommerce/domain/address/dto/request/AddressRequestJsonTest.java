package com.build.ecommerce.domain.address.dto.request;

import com.build.ecommerce.domain.address.entity.AddressType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressRequestJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesAddressTypeByEnumName() throws Exception {
        String json = """
                {
                  "addressType": "REGION_ADDR",
                  "address": "서울",
                  "extraAddress": "101호",
                  "zipCode": "12345"
                }
                """;
        AddressRequest req = objectMapper.readValue(json, AddressRequest.class);
        assertThat(req.addressType()).isEqualTo(AddressType.REGION_ADDR);
    }

    @Test
    void unknownAddressTypeStringBecomesNull() throws Exception {
        String json = """
                {
                  "addressType": "INVALID",
                  "address": "서울",
                  "extraAddress": "101호",
                  "zipCode": "12345"
                }
                """;
        AddressRequest req = objectMapper.readValue(json, AddressRequest.class);
        assertThat(req.addressType()).isNull();
    }
}
