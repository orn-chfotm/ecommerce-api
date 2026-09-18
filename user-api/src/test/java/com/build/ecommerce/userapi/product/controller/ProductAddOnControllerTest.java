package com.build.ecommerce.userapi.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductAddOnRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.product.service.ProductAddOnService;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * user-api에는 추가구성상품 등록(admin 쓰기) 엔드포인트가 없으므로,
 * 셋업은 domain의 ProductAddOnService를 직접 주입받아 수행한다.
 */
class ProductAddOnControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductAddOnService productAddOnService;

    private Product createProduct(String name, ProductType productType, BigDecimal price) {
        return createProduct(name, productType, price, true);
    }

    private Product createProduct(String name, ProductType productType, BigDecimal price, boolean active) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                name,
                "추가구성상품 조회 테스트용 상품",
                price,
                100,
                1,
                active,
                productType,
                null,
                null
        ).toEntity());
    }

    @Test
    @DisplayName("추가구성상품 목록 조회 성공 - 등록 순서(sortOrder)대로 반환된다")
    void getAddOnsForUserTest() throws Exception {
        Product product = createProduct("사용자 본품", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product first = createProduct("사용자 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000));
        Product second = createProduct("사용자 보관함", ProductType.ADD_ON, BigDecimal.valueOf(5000));

        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(first.getId()));
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(second.getId()));

        mockMvc.perform(get("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].addOnProductId").value(first.getId()))
                .andExpect(jsonPath("$.data[0].name").value("사용자 세탁망"))
                .andExpect(jsonPath("$.data[0].price").value(3000))
                .andExpect(jsonPath("$.data[0].sortOrder").value(0))
                .andExpect(jsonPath("$.data[1].addOnProductId").value(second.getId()))
                .andExpect(jsonPath("$.data[1].sortOrder").value(1));
    }

    @Test
    @DisplayName("추가구성상품 목록 조회 - 등록된 추가구성상품이 없으면 빈 목록을 반환한다")
    void getAddOnsForUserEmptyTest() throws Exception {
        Product product = createProduct("추가구성 없는 본품", ProductType.NORMAL, BigDecimal.valueOf(20000));

        mockMvc.perform(get("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("추가구성상품 목록 조회 실패 - 본품이 ADD_ON 유형이면 존재를 숨긴다")
    void getAddOnsForUserAddOnProductNotFoundTest() throws Exception {
        Product addOnProduct = createProduct("단독 노출 금지 추가구성", ProductType.ADD_ON, BigDecimal.valueOf(3000));

        mockMvc.perform(get("/v1/product/{productId}/add-ons", addOnProduct.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ProductExceptionCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 목록 조회 - 비노출 추가구성상품은 결과에서 제외된다")
    void getAddOnsForUserExcludesInactiveAddOnTest() throws Exception {
        Product product = createProduct("비노출 애드온 본품", ProductType.NORMAL, BigDecimal.valueOf(20000));
        Product visibleAddOn = createProduct("노출 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), true);
        Product hiddenAddOn = createProduct("비노출 보관함", ProductType.ADD_ON, BigDecimal.valueOf(5000), false);

        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(visibleAddOn.getId()));
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(hiddenAddOn.getId()));

        mockMvc.perform(get("/v1/product/{productId}/add-ons", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].addOnProductId").value(visibleAddOn.getId()));
    }
}
