package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("제품 등록")
    void productInsertTest() throws Exception {
        var reqeust = multipart("/v1/product")
                .param("category", "fashion")
                .param("name", "장갑")
                .param("description", "따뜻한 장갑")
                .param("price", "10000")
                .param("stockQuantity", "100")
                .param("minOrderQuantity", "1")
                .param("active", "true");

        mockMvc.perform(reqeust
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 실패")
    void productInsertFailTest() throws Exception {
        mockMvc.perform(multipart("/v1/product")
                        .param("category", "")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        mockMvc.perform(multipart("/v1/product")
                        .param("category", "fasion")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());


    }

    @Test
    @DisplayName("제품 리스트 GET")
    void productListTest() throws Exception {

        ProductRequest request = new ProductRequest(
                ProductCategoryType.FASHION,
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );

        for (int i = 0; i < 10; i++) {
            productRepository.save(request.toEntity());
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

    @Test
    @DisplayName("제품 상세 조회")
    void productDetailTest() throws Exception {
        ProductRequest request = new ProductRequest(
                ProductCategoryType.FASHION,
                "장갑",
                "따뜻한 장갑",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                null
        );
        Product product = request.toEntity();
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/v1/product/{productId}", saved.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 상세 조회 실패 - 존재하지 않는 제품")
    void productDetailFailTest() throws Exception {
        mockMvc.perform(get("/v1/product/{productId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}