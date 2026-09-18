package com.build.ecommerce.adminapi.product.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.product.dto.request.ProductAddOnRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionGroupRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductAddOnControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(String name, ProductType productType, BigDecimal price) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                name,
                "추가구성상품 테스트용 상품",
                price,
                100,
                1,
                true,
                productType,
                null,
                null
        ).toEntity());
    }

    private void registerAddOn(Long productId, Long addOnProductId) throws Exception {
        mockMvc.perform(post("/v1/product/{productId}/add-ons", productId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(addOnProductId))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("추가구성상품 등록 성공")
    void registerAddOnTest() throws Exception {
        Product product = createProduct("본품 티셔츠", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product addOnProduct = createProduct("세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));

        mockMvc.perform(post("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(addOnProduct.getId()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productAddOnId").isNumber())
                .andExpect(jsonPath("$.data.addOnProductId").value(addOnProduct.getId()))
                .andExpect(jsonPath("$.data.name").value("세탁망"))
                .andExpect(jsonPath("$.data.price").value(3000))
                .andExpect(jsonPath("$.data.stockQuantity").value(100))
                .andExpect(jsonPath("$.data.sortOrder").value(0));
    }

    @Test
    @DisplayName("추가구성상품 목록 조회 - 등록 순서대로 sortOrder가 0,1,2로 끝에 추가된다")
    void getAddOnsSortOrderTest() throws Exception {
        Product product = createProduct("본품 티셔츠", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product first = createProduct("세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));
        Product second = createProduct("보관함", ProductType.ADD_ON, BigDecimal.valueOf(5000));
        Product third = createProduct("방향제", ProductType.ADD_ON, BigDecimal.valueOf(7000));

        registerAddOn(product.getId(), first.getId());
        registerAddOn(product.getId(), second.getId());
        registerAddOn(product.getId(), third.getId());

        mockMvc.perform(get("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].addOnProductId").value(first.getId()))
                .andExpect(jsonPath("$.data[0].sortOrder").value(0))
                .andExpect(jsonPath("$.data[1].addOnProductId").value(second.getId()))
                .andExpect(jsonPath("$.data[1].sortOrder").value(1))
                .andExpect(jsonPath("$.data[2].addOnProductId").value(third.getId()))
                .andExpect(jsonPath("$.data[2].sortOrder").value(2));
    }

    @Test
    @DisplayName("추가구성상품 등록 실패 - 자기 자신을 등록")
    void registerAddOnSelfReferenceTest() throws Exception {
        Product product = createProduct("본품 티셔츠", ProductType.NORMAL, BigDecimal.valueOf(20000));

        mockMvc.perform(post("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(product.getId()))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_SELF_REFERENCE_NOT_ALLOWED.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 등록 실패 - 대상이 일반 상품 유형")
    void registerAddOnTargetNotAddOnTypeTest() throws Exception {
        Product product = createProduct("본품 티셔츠", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product normalTarget = createProduct("일반 상품", ProductType.NORMAL, BigDecimal.valueOf(3000));

        mockMvc.perform(post("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(normalTarget.getId()))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_TARGET_NOT_ADD_ON_TYPE.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 등록 실패 - 본품이 추가구성상품 유형")
    void registerAddOnParentNotNormalTypeTest() throws Exception {
        Product addOnParent = createProduct("세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));
        Product addOnProduct = createProduct("보관함", ProductType.ADD_ON, BigDecimal.valueOf(5000));

        mockMvc.perform(post("/v1/product/{productId}/add-ons", addOnParent.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(addOnProduct.getId()))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_PARENT_NOT_NORMAL_TYPE.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 등록 실패 - 이미 등록된 추가구성상품")
    void registerAddOnAlreadyRegisteredTest() throws Exception {
        Product product = createProduct("본품 티셔츠", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product addOnProduct = createProduct("세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));

        registerAddOn(product.getId(), addOnProduct.getId());

        mockMvc.perform(post("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new ProductAddOnRequest(addOnProduct.getId()))))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_ALREADY_REGISTERED.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 옵션 등록 실패 - ADD_ON 상품에는 옵션을 등록할 수 없다")
    void registerOptionsOnAddOnProductTest() throws Exception {
        Product addOnProduct = createProduct("세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));

        ProductOptionRegisterRequest optionRequest = new ProductOptionRegisterRequest(
                List.of(new ProductOptionGroupRequest("사이즈", 0, List.of("M"))),
                List.of(new ProductOptionVariantRequest("SKU-M", 10, BigDecimal.ZERO, null,
                        List.of(new ProductOptionVariantValueRequest("사이즈", "M"))))
        );

        mockMvc.perform(post("/v1/product/{productId}/options", addOnProduct.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(optionRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_PRODUCT_OPTION_NOT_ALLOWED.getMessage()));
    }
}
