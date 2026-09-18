package com.build.ecommerce.userapi.product.controller;

import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductStatusType;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.file.entity.FileDetail;
import com.build.ecommerce.domain.file.entity.FileMaster;
import com.build.ecommerce.domain.file.enums.FileMasterType;
import com.build.ecommerce.domain.file.repository.FileMasterRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
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
                null,
                null,
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
                .andExpect(status().isOk())
                // status가 NULL인 상품이 노출 게이트(삼값논리)에서 통째로 탈락하는 회귀를 잡기 위한 안전망
                .andExpect(jsonPath("$.data.content.length()").value(org.hamcrest.Matchers.greaterThan(0)));
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
                null,
                null,
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
                null,
                null,
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

    private Product createProduct(String name, ProductType productType) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                name,
                "추가구성상품 노출 테스트용 상품",
                BigDecimal.valueOf(10000L),
                100,
                1,
                true,
                productType,
                null,
                null
        ).toEntity());
    }

    private Product createProduct(String name, boolean active, LocalDateTime displayStartAt) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                name,
                "사용자 노출 게이트 테스트용 상품",
                BigDecimal.valueOf(10000L),
                100,
                1,
                active,
                ProductType.NORMAL,
                displayStartAt,
                null
        ).toEntity());
    }

    @Test
    @DisplayName("제품 리스트 GET - 추가구성상품(ADD_ON)은 노출되지 않는다")
    void productListExcludesAddOnTest() throws Exception {
        String namePrefix = "애드온노출테스트";
        Product normalProduct = createProduct(namePrefix + "-본품", ProductType.NORMAL);
        createProduct(namePrefix + "-추가구성", ProductType.ADD_ON);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", namePrefix)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].productId").value(normalProduct.getId()));
    }

    @Test
    @DisplayName("제품 상세 조회 실패 - 추가구성상품(ADD_ON)은 상세 조회되지 않는다")
    void productDetailAddOnNotFoundTest() throws Exception {
        Product addOnProduct = createProduct("애드온상세테스트-추가구성", ProductType.ADD_ON);

        mockMvc.perform(get("/v1/product/{productId}", addOnProduct.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ProductExceptionCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("제품 리스트 GET - 비노출(active=false) 상품은 제외된다")
    void productListExcludesInactiveTest() throws Exception {
        String namePrefix = "비노출필터테스트";
        Product visibleProduct = createProduct(namePrefix + "-노출", true, null);
        Product hiddenProduct = createProduct(namePrefix + "-비노출", false, null);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", namePrefix)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].productId", hasItem(visibleProduct.getId().intValue())))
                .andExpect(jsonPath("$.data.content[*].productId", not(hasItem(hiddenProduct.getId().intValue()))));
    }

    @Test
    @DisplayName("제품 리스트 GET - 삭제된 상품은 제외된다")
    void productListExcludesDeletedTest() throws Exception {
        String namePrefix = "삭제필터테스트";
        Product visibleProduct = createProduct(namePrefix + "-정상", true, null);
        Product deletedProduct = createProduct(namePrefix + "-삭제", true, null);
        deletedProduct.markDelete();
        productRepository.save(deletedProduct);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", namePrefix)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].productId").value(visibleProduct.getId()));
    }

    @Test
    @DisplayName("제품 리스트 GET - displayStartAt이 미래면 제외되고, 과거/null이면 노출된다")
    void productListFiltersByDisplayStartAtTest() throws Exception {
        String namePrefix = "노출시점필터테스트";
        Product futureProduct = createProduct(namePrefix + "-미래", true, LocalDateTime.now().plusDays(1));
        Product pastProduct = createProduct(namePrefix + "-과거", true, LocalDateTime.now().minusDays(1));
        Product noDisplayStartProduct = createProduct(namePrefix + "-미지정", true, null);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", namePrefix)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[*].productId", hasItem(pastProduct.getId().intValue())))
                .andExpect(jsonPath("$.data.content[*].productId", hasItem(noDisplayStartProduct.getId().intValue())))
                .andExpect(jsonPath("$.data.content[*].productId", not(hasItem(futureProduct.getId().intValue()))));
    }

    @Test
    @DisplayName("제품 리스트 GET - 매진/판매중지 상품도 노출된다")
    void productListIncludesSoldOutAndStoppedTest() throws Exception {
        String soldOutName = "상태노출테스트-매진";
        String stoppedName = "상태노출테스트-판매중지";

        Product soldOutProduct = createProduct(soldOutName, true, null);
        soldOutProduct.changeStatus(ProductStatusType.SOLD_OUT);
        productRepository.save(soldOutProduct);

        Product stoppedProduct = createProduct(stoppedName, true, null);
        stoppedProduct.changeStatus(ProductStatusType.STOPPED);
        productRepository.save(stoppedProduct);

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", soldOutName)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].productId").value(soldOutProduct.getId()))
                .andExpect(jsonPath("$.data.content[0].orderable").value(false));

        mockMvc.perform(get("/v1/product")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .param("name", stoppedName)
                        .param("page", "0")
                        .param("size", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].productId").value(stoppedProduct.getId()))
                .andExpect(jsonPath("$.data.content[0].orderable").value(false));
    }

    @Test
    @DisplayName("제품 상세 조회 실패 - 비노출 상품은 404")
    void productDetailInactiveNotFoundTest() throws Exception {
        Product hiddenProduct = createProduct("비노출상세테스트", false, null);

        mockMvc.perform(get("/v1/product/{productId}", hiddenProduct.getId())
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ProductExceptionCode.PRODUCT_NOT_FOUND.getMessage()));
    }
}
