package com.build.ecommerce.domain.address.controller;

import com.build.ecommerce.domain.address.dto.request.AddressRequest;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.build.ecommerce.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerTest extends UnitTestHelper {

    @Test
    @DisplayName("사용자 배송지 등록 성공")
    void registerAddressTest() throws Exception {
        AddressRequest request = new AddressRequest(
                AddressType.REGION_ADDR,
                "서울특별시 강남구 테헤란로",
                "101동 1203호",
                "06236"
        );

        mockMvc.perform(post("/v1/address")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addressId").isNumber())
                .andExpect(jsonPath("$.data.addressType").value(request.addressType().name()))
                .andExpect(jsonPath("$.data.address").value(request.address()))
                .andExpect(jsonPath("$.data.extraAddress").value(request.extraAddress()))
                .andExpect(jsonPath("$.data.zipCode").value(request.zipCode()));

        mockMvc.perform(get("/v1/address")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addressList[*].addressType", hasItem(request.addressType().name())))
                .andExpect(jsonPath("$.data.addressList[*].address", hasItem(request.address())))
                .andExpect(jsonPath("$.data.addressList[*].extraAddress", hasItem(request.extraAddress())))
                .andExpect(jsonPath("$.data.addressList[*].zipCode", hasItem(request.zipCode())));
    }

    @Test
    @DisplayName("사용자 배송지 2건 등록 후 정보 조회 성공 테스트")
    void getAddressListTest() throws Exception {
        AddressRequest firstRequest = new AddressRequest(
                AddressType.ROAD_ADDR,
                "서울특별시 서초구 서초대로",
                "202동 303호",
                "06611"
        );
        AddressRequest secondRequest = new AddressRequest(
                AddressType.REGION_ADDR,
                "서울특별시 송파구 올림픽로",
                "303동 404호",
                "05552"
        );

        mockMvc.perform(post("/v1/address")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/address")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/address")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addressList").isArray())
                .andExpect(jsonPath("$.data.addressList.length()").value(2))
                .andExpect(jsonPath("$.data.addressList[*].addressId").exists())
                .andExpect(jsonPath("$.data.addressList[*].addressType", hasItem(firstRequest.addressType().name())))
                .andExpect(jsonPath("$.data.addressList[*].address", hasItem(firstRequest.address())))
                .andExpect(jsonPath("$.data.addressList[*].extraAddress", hasItem(firstRequest.extraAddress())))
                .andExpect(jsonPath("$.data.addressList[*].zipCode", hasItem(firstRequest.zipCode())))
                .andExpect(jsonPath("$.data.addressList[*].addressType", hasItem(secondRequest.addressType().name())))
                .andExpect(jsonPath("$.data.addressList[*].address", hasItem(secondRequest.address())))
                .andExpect(jsonPath("$.data.addressList[*].extraAddress", hasItem(secondRequest.extraAddress())))
                .andExpect(jsonPath("$.data.addressList[*].zipCode", hasItem(secondRequest.zipCode())));
    }
}
