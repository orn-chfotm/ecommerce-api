package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("제품 등록")
    void productInsertTest() throws Exception {
        ProductRequest request = new ProductRequest(
                "fashion",
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );

        mockMvc.perform(post("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 실패")
    void productInsertFailTest() throws Exception {
        // Given -> 사용자의 값 누락
        ProductRequest userRequestFail = new ProductRequest(
                "",
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );

        // Given -> fashion 이 맞는 Enum 값 FE Option 값 에러 500으로 처리
        ProductRequest frontRequestFail = new ProductRequest(
                "fasion",
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );

        // when & then
        mockMvc.perform(post("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(userRequestFail)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(frontRequestFail)))
                .andDo(print())
                .andExpect(status().is5xxServerError());


    }

    @Test
    @DisplayName("제품 리스트 GET")
    void productListTest() throws Exception {

        ProductRequest request = new ProductRequest(
                "fashion",
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );

        for (int i = 0; i < 10; i++) {
            Product product = request.toEntity(request);
            productRepository.save(product);
        }

        // given
        ProductSearchRequest serchRequest = new ProductSearchRequest(
                null,
                null,
                0,
                100000,
                0
        );

        // when & then
        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(serchRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}