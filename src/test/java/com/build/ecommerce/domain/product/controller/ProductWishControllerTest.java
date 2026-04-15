package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductWishRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.helper.UnitTestHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductWishControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("찜하기 등록 성공")
    void registerProductWishTest() throws Exception {
        Long savedProductId = createProduct().getId();
        ProductWishRequest request = new ProductWishRequest(savedProductId);

        mockMvc.perform(post("/v1/wish")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productWishId").isNumber())
                .andExpect(jsonPath("$.data.product.productId").value(savedProductId));
    }

    @Test
    @DisplayName("찜하기 리스트 조회 성공")
    void selectProductWishListTest() throws Exception {
        Long savedProductId = createProduct().getId();
        createWish(savedProductId);

        mockMvc.perform(get("/v1/wish")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].product.productId", hasItem(savedProductId.intValue())));
    }

    @Test
    @DisplayName("찜하기 상세 조회 성공")
    void selectProductWishDetailTest() throws Exception {
        Long savedProductId = createProduct().getId();
        Long productWishId = createWish(savedProductId);

        mockMvc.perform(get("/v1/wish/{productWishId}", productWishId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productWishId").value(productWishId))
                .andExpect(jsonPath("$.data.product.productId").value(savedProductId));
    }

    @Test
    @DisplayName("찜하기 삭제 성공")
    void deleteProductWishTest() throws Exception {
        Long savedProductId = createProduct().getId();
        Long productWishId = createWish(savedProductId);

        mockMvc.perform(delete("/v1/wish/{productWishId}", productWishId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productWishId").value(productWishId));

        mockMvc.perform(get("/v1/wish/{productWishId}", productWishId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("찜하기 등록 실패 - 존재하지 않는 제품")
    void registerProductWishFailByNotFoundProductTest() throws Exception {
        ProductWishRequest request = new ProductWishRequest(999999L);

        mockMvc.perform(post("/v1/wish")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("찜하기 삭제 실패 - 내가 찜하지 않은 제품")
    void deleteProductWishFailByNotFoundWishTest() throws Exception {
        mockMvc.perform(delete("/v1/wish/{productWishId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Product createProduct() {
        Product product = Product.builder()
                .category(ProductCategoryType.FASHION)
                .name("테스트 장갑")
                .description("찜하기 테스트용 상품")
                .price(BigDecimal.valueOf(10000L))
                .stockQuantity(50)
                .minOrderQuantity(1)
                .active(true)
                .build();

        return productRepository.save(product);
    }

    private Long createWish(Long productId) throws Exception {
        ProductWishRequest request = new ProductWishRequest(productId);

        MvcResult result = mockMvc.perform(post("/v1/wish")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("data").get("productWishId").asLong();
    }
}
