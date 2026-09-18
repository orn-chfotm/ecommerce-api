package com.build.ecommerce.adminapi.product.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductUpdateRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("제품 등록 - 파일 없음")
    void productInsertTest() throws Exception {
        mockMvc.perform(multipart("/v1/product")
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 - 파일 포함")
    void productInsertWithFilesTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "image1.jpg", "image/jpeg", "test-image-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "image2.jpg", "image/jpeg", "test-image-2".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file1)
                        .file(file2)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 실패 - 파일 개수 초과")
    void productInsertFileExceedTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "image1.jpg", "image/jpeg", "test-image-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "image2.jpg", "image/jpeg", "test-image-2".getBytes());
        MockMultipartFile file3 = new MockMultipartFile(
                "files", "image3.jpg", "image/jpeg", "test-image-3".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("제품 등록 실패 - 허용되지 않는 파일 형식")
    void productInsertInvalidExtensionTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "script.exe", "application/octet-stream", "malicious".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("제품 등록 실패 - 필수값 누락")
    void productInsertFailTest() throws Exception {
        mockMvc.perform(multipart("/v1/product")
                        .param("category", "")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
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
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private long registerProductReturningId() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/v1/product")
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("productId").asLong();
    }

    @Test
    @DisplayName("제품 상세 수정 성공")
    void productUpdateTest() throws Exception {
        long productId = registerProductReturningId();
        ProductUpdateRequest request = new ProductUpdateRequest(
                ProductCategoryType.FOOD, "수정된 장갑", "수정된 설명",
                BigDecimal.valueOf(20000), 50, 2, false, null, null);

        mockMvc.perform(patch("/v1/product/{productId}", productId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 장갑"))
                .andExpect(jsonPath("$.data.category").value("FOOD"))
                .andExpect(jsonPath("$.data.price").value(20000))
                .andExpect(jsonPath("$.data.stockQuantity").value(50))
                .andExpect(jsonPath("$.data.minOrderQuantity").value(2))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("제품 상세 수정 실패 - 존재하지 않는 제품")
    void productUpdateNotFoundTest() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest(
                ProductCategoryType.FOOD, "수정된 장갑", "수정된 설명",
                BigDecimal.valueOf(20000), 50, 2, false, null, null);

        mockMvc.perform(patch("/v1/product/{productId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("제품 상세 수정 실패 - 필수값 누락")
    void productUpdateValidationFailTest() throws Exception {
        long productId = registerProductReturningId();
        ProductUpdateRequest request = new ProductUpdateRequest(
                ProductCategoryType.FOOD, "", "수정된 설명",
                BigDecimal.valueOf(20000), 50, 2, false, null, null);

        mockMvc.perform(patch("/v1/product/{productId}", productId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("관리자 상품 목록 조회는 비노출/삭제 상품도 포함한다")
    void getProductListIncludesInactiveTest() throws Exception {
        // 관리자 목록은 노출 여부와 무관하게 조회돼야 하므로 active=false 상품을 직접 저장한다.
        String productName = "관리자목록비노출상품";
        Product inactiveProduct = productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                productName,
                "비노출 상품",
                BigDecimal.valueOf(10000),
                100,
                1,
                false,
                null,
                null,
                null
        ).toEntity());

        mockMvc.perform(get("/v1/product")
                        .param("name", productName)
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].productId").value(inactiveProduct.getId()))
                .andExpect(jsonPath("$.data.content[0].active").value(false));
    }

    @Test
    @DisplayName("제품 삭제 성공 - 삭제 시 active가 false로 전환된다")
    void productDeleteTest() throws Exception {
        long productId = registerProductReturningId();

        mockMvc.perform(delete("/v1/product/{productId}", productId)
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.active").value(false));
    }
}
