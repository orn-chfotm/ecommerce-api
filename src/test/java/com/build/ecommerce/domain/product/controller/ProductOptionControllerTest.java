package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductOptionAxisRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantStockRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.helper.UnitTestHelper;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductOptionControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct() {
        ProductRequest request = new ProductRequest(
                ProductCategoryType.FASHION,
                "티셔츠",
                "기본 티셔츠",
                BigDecimal.valueOf(20000L),
                100,
                1,
                true,
                null
        );
        return productRepository.save(request.toEntity());
    }

    private ProductOptionRegisterRequest sampleOptionRequest() {
        return new ProductOptionRegisterRequest(
                List.of(
                        new ProductOptionAxisRequest("색상", 0, List.of("블랙", "화이트")),
                        new ProductOptionAxisRequest("사이즈", 1, List.of("S", "M"))
                ),
                List.of(
                        new ProductOptionVariantRequest("TSHIRT-BLACK-S", 10, BigDecimal.ZERO, null,
                                List.of(new ProductOptionVariantValueRequest("색상", "블랙"), new ProductOptionVariantValueRequest("사이즈", "S"))),
                        new ProductOptionVariantRequest("TSHIRT-WHITE-M", 5, BigDecimal.valueOf(1000), 3,
                                List.of(new ProductOptionVariantValueRequest("색상", "화이트"), new ProductOptionVariantValueRequest("사이즈", "M")))
                )
        );
    }

    @Test
    @DisplayName("상품 옵션 등록 성공")
    void registerProductOptionsTest() throws Exception {
        Product product = createProduct();

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasOptions").value(true))
                .andExpect(jsonPath("$.data.options.length()").value(2))
                .andExpect(jsonPath("$.data.variants.length()").value(2))
                .andExpect(jsonPath("$.data.variants[0].optionValues.length()").value(2));
    }

    @Test
    @DisplayName("상품 옵션 등록 실패 - 관리자 권한 없음")
    void registerProductOptionsForbiddenTest() throws Exception {
        Product product = createProduct();

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("상품 옵션 등록 실패 - 이미 옵션이 등록된 상품")
    void registerProductOptionsAlreadyRegisteredTest() throws Exception {
        Product product = createProduct();

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("상품 옵션 등록 실패 - 존재하지 않는 옵션 명 참조")
    void registerProductOptionsInvalidAxisTest() throws Exception {
        Product product = createProduct();

        ProductOptionRegisterRequest invalidRequest = new ProductOptionRegisterRequest(
                List.of(new ProductOptionAxisRequest("색상", 0, List.of("블랙"))),
                List.of(new ProductOptionVariantRequest("SKU-1", 10, null, null,
                        List.of(new ProductOptionVariantValueRequest("사이즈", "S"))))
        );

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("상품 옵션 조회 - 옵션 미등록 상품")
    void getProductOptionsEmptyTest() throws Exception {
        Product product = createProduct();

        mockMvc.perform(get("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasOptions").value(false))
                .andExpect(jsonPath("$.data.options.length()").value(0))
                .andExpect(jsonPath("$.data.variants.length()").value(0));
    }

    @Test
    @DisplayName("상품 옵션 조회 성공")
    void getProductOptionsTest() throws Exception {
        Product product = createProduct();

        mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasOptions").value(true))
                .andExpect(jsonPath("$.data.options.length()").value(2))
                .andExpect(jsonPath("$.data.variants.length()").value(2));
    }

    private long registerOptionsReturningFirstVariantId(Product product) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sampleOptionRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("variants").get(0).get("productOptionVariantId").asLong();
    }

    @Test
    @DisplayName("옵션 조합 재고 수정 성공")
    void updateVariantStockTest() throws Exception {
        Product product = createProduct();
        long variantId = registerOptionsReturningFirstVariantId(product);

        mockMvc.perform(patch("/v1/product/{productId}/options/variants/{variantId}/stock", product.getId(), variantId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductOptionVariantStockRequest(30))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stockQuantity").value(30));
    }

    @Test
    @DisplayName("옵션 조합 재고 수정 실패 - 관리자 권한 없음")
    void updateVariantStockForbiddenTest() throws Exception {
        Product product = createProduct();
        long variantId = registerOptionsReturningFirstVariantId(product);

        mockMvc.perform(patch("/v1/product/{productId}/options/variants/{variantId}/stock", product.getId(), variantId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductOptionVariantStockRequest(30))))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("옵션 조합 재고 수정 실패 - 음수 재고")
    void updateVariantStockNegativeTest() throws Exception {
        Product product = createProduct();
        long variantId = registerOptionsReturningFirstVariantId(product);

        mockMvc.perform(patch("/v1/product/{productId}/options/variants/{variantId}/stock", product.getId(), variantId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductOptionVariantStockRequest(-1))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("옵션 조합 재고 수정 실패 - 다른 상품의 옵션 조합")
    void updateVariantStockWrongProductTest() throws Exception {
        Product product = createProduct();
        long variantId = registerOptionsReturningFirstVariantId(product);

        Product otherProduct = createProduct();

        mockMvc.perform(patch("/v1/product/{productId}/options/variants/{variantId}/stock", otherProduct.getId(), variantId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductOptionVariantStockRequest(30))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
