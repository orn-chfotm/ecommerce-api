package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.domain.product.dto.request.ProductOptionAxisRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.service.ProductOptionService;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductOptionControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionService productOptionService;

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
        productOptionService.registerProductOptions(product.getId(), sampleOptionRequest());

        mockMvc.perform(get("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasOptions").value(true))
                .andExpect(jsonPath("$.data.options.length()").value(2))
                .andExpect(jsonPath("$.data.variants.length()").value(2));
    }
}
