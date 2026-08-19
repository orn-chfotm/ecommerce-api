package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.domain.cart.dto.request.CartRequest;
import com.build.ecommerce.domain.cart.dto.request.CartUpdateRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionAxisRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.service.ProductOptionService;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionService productOptionService;

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
        CartRequest request = new CartRequest(productId, null, quantity);
        MvcResult result = mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("cartId").asLong();
    }

    private long registerOptionsReturningFirstVariantId(Product product) throws Exception {
        ProductOptionRegisterRequest optionRequest = new ProductOptionRegisterRequest(
                List.of(new ProductOptionAxisRequest("사이즈", 0, List.of("S", "M"))),
                List.of(
                        new ProductOptionVariantRequest("SIZE-S", 10, BigDecimal.ZERO, null,
                                List.of(new ProductOptionVariantValueRequest("사이즈", "S"))),
                        new ProductOptionVariantRequest("SIZE-M", 3, BigDecimal.valueOf(1000), null,
                                List.of(new ProductOptionVariantValueRequest("사이즈", "M")))
                )
        );

        return productOptionService.registerProductOptions(product.getId(), optionRequest)
                .variants().get(0).productOptionVariantId();
    }

    @Test
    @DisplayName("장바구니 상품 추가")
    void addCartTest() throws Exception {
        Product product = createProduct(100);
        CartRequest request = new CartRequest(product.getId(), null, 2);

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

        CartRequest request = new CartRequest(product.getId(), null, 3);
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
        CartRequest request = new CartRequest(product.getId(), null, 10);

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

    @Test
    @DisplayName("장바구니 상품 추가 - 옵션 조합 선택")
    void addCartWithOptionTest() throws Exception {
        Product product = createProduct(100);
        long variantId = registerOptionsReturningFirstVariantId(product);

        CartRequest request = new CartRequest(product.getId(), variantId, 2);
        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productOptionVariantId").value(variantId))
                .andExpect(jsonPath("$.data.sku").value("SIZE-S"))
                .andExpect(jsonPath("$.data.selectedOptions.length()").value(1))
                .andExpect(jsonPath("$.data.stockQuantity").value(10));
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 옵션 등록 상품에 옵션 미지정")
    void addCartMissingOptionTest() throws Exception {
        Product product = createProduct(100);
        registerOptionsReturningFirstVariantId(product);

        CartRequest request = new CartRequest(product.getId(), null, 2);
        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 옵션 미등록 상품에 옵션 지정")
    void addCartUnexpectedOptionTest() throws Exception {
        Product optionedProduct = createProduct(100);
        long variantId = registerOptionsReturningFirstVariantId(optionedProduct);
        Product plainProduct = createProduct(100);

        CartRequest request = new CartRequest(plainProduct.getId(), variantId, 2);
        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("장바구니 상품 추가 - 같은 상품 다른 옵션은 별도 항목으로 담김")
    void addCartDifferentOptionsSeparateEntriesTest() throws Exception {
        Product product = createProduct(100);
        long variantId = registerOptionsReturningFirstVariantId(product);

        JsonNode optionsRoot = objectMapper.readTree(
                mockMvc.perform(get("/v1/product/{productId}/options", product.getId())
                                .headers(getHeaderSetting())
                                .headers(getAccessToken()))
                        .andReturn().getResponse().getContentAsString());
        long otherVariantId = optionsRoot.get("data").get("variants").get(1).get("productOptionVariantId").asLong();

        MvcResult firstResult = mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(new CartRequest(product.getId(), variantId, 1))))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        long firstCartId = objectMapper.readTree(firstResult.getResponse().getContentAsString())
                .get("data").get("cartId").asLong();

        MvcResult secondResult = mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(new CartRequest(product.getId(), otherVariantId, 1))))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        long secondCartId = objectMapper.readTree(secondResult.getResponse().getContentAsString())
                .get("data").get("cartId").asLong();

        assertThat(firstCartId).isNotEqualTo(secondCartId);

        MvcResult listResult = mockMvc.perform(get("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Long> variantIdsInCart = new java.util.ArrayList<>();
        for (JsonNode node : objectMapper.readTree(listResult.getResponse().getContentAsString()).get("data")) {
            long cartId = node.get("cartId").asLong();
            if (cartId == firstCartId || cartId == secondCartId) {
                variantIdsInCart.add(node.get("productOptionVariantId").asLong());
            }
        }
        assertThat(variantIdsInCart).containsExactlyInAnyOrder(variantId, otherVariantId);
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 옵션 조합 재고 부족")
    void addCartOptionNotEnoughStockTest() throws Exception {
        Product product = createProduct(100);
        long variantId = registerOptionsReturningFirstVariantId(product);

        CartRequest request = new CartRequest(product.getId(), variantId, 999);
        mockMvc.perform(post("/v1/cart")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
}
