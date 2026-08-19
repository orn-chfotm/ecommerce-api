package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.infra.file.entity.FileDetail;
import com.build.ecommerce.infra.file.entity.FileMaster;
import com.build.ecommerce.infra.file.enums.FileMasterType;
import com.build.ecommerce.infra.persistence.file.FileMasterRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends UnitTestHelper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileMasterRepository fileMasterRepository;

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
    @DisplayName("제품 리스트 GET - 첨부파일이 있는 제품도 files가 배치 조회로 채워진다")
    void productListWithFilesTest() throws Exception {
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
        Product product = productRepository.save(request.toEntity());

        FileMaster fileMaster = FileMaster.builder()
                .referenceType(FileMasterType.PRODUCT)
                .build();
        fileMaster.addFileDetail(FileDetail.builder()
                .sortOrder(0)
                .storedFileName("stored-glove.jpg")
                .originalFileName("glove.jpg")
                .extension("jpg")
                .fileSize(1024L)
                .path("/files/stored-glove.jpg")
                .build());
        fileMasterRepository.save(fileMaster);

        product.attachFiles(fileMaster);
        productRepository.save(product);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("page", "0")
                        .param("size", "200"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[?(@.productId == " + product.getId() + ")].files[0].originalFileName")
                        .value("glove.jpg"));
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
