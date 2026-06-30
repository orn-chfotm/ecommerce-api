package com.build.ecommerce.domain.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.helper.UnitTestHelper;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("minPrice", "0")
                        .param("maxPrice", "100000")
                        .param("stockQuantity", "0")
                        .param("page", "0")
                        .param("size", "5"))
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
