package com.build.ecommerce.domain.cart.controller;

import com.build.ecommerce.domain.cart.dto.request.CartRequest;
import com.build.ecommerce.domain.cart.dto.request.CartUpdateRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.helper.UnitTestHelper;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(int stockQuantity) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                "테스트상품",
                "설명",
                BigDecimal.valueOf(10000),
                stockQuantity,
                1,
                true,
                null
        ).toEntity());
    }

    private Long addToCart(Long productId, int quantity) throws Exception {
        CartRequest request = new CartRequest(productId, quantity);
        MvcResult result = mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("cartId").asLong();
    }

    @Test
    @DisplayName("장바구니 상품 추가")
    void addCartTest() throws Exception {
        Product product = createProduct(100);
        CartRequest request = new CartRequest(product.getId(), 2);

        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 상품 추가 - 이미 담긴 상품 수량 합산")
    void addCartMergeTest() throws Exception {
        Product product = createProduct(100);
        addToCart(product.getId(), 5);

        CartRequest request = new CartRequest(product.getId(), 3);
        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 재고 부족")
    void addCartNotEnoughStockTest() throws Exception {
        Product product = createProduct(3);
        CartRequest request = new CartRequest(product.getId(), 10);

        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("장바구니 목록 조회")
    void getCartsTest() throws Exception {
        mockMvc.perform(get("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 수량 수정")
    void updateCartTest() throws Exception {
        Product product = createProduct(100);
        Long cartId = addToCart(product.getId(), 2);

        CartUpdateRequest request = new CartUpdateRequest(5);
        mockMvc.perform(patch("/v1/cart/{cartId}", cartId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 수량 수정 실패 - 상품 품절")
    void updateCartSoldOutTest() throws Exception {
        Product product = createProduct(1);
        Long cartId = addToCart(product.getId(), 1);

        // 재고 소진 (장바구니 담은 후 다른 주문으로 품절된 상황 시뮬레이션)
        product.removeStock(1);
        productRepository.save(product);

        CartUpdateRequest request = new CartUpdateRequest(1);
        mockMvc.perform(patch("/v1/cart/{cartId}", cartId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("장바구니 상품 단건 삭제")
    void removeCartTest() throws Exception {
        Product product = createProduct(100);
        Long cartId = addToCart(product.getId(), 2);

        mockMvc.perform(delete("/v1/cart/{cartId}", cartId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 전체 비우기")
    void clearCartTest() throws Exception {
        mockMvc.perform(delete("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
