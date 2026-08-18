package com.build.ecommerce.domain.order.controller;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionAxisRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.helper.UnitTestHelper;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.infra.persistence.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class OrderControllerTest extends UnitTestHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionVariantRepository productOptionVariantRepository;

    /**
     * @BeforeAll 로 만든 test@email.com 사용자로 주문 1건 생성 후 응답의 주문 PK 반환.
     * (테스트마다 롤백돼도 PK 시퀀스는 증가하므로 취소 테스트 등에서는 하드코딩 1 대신 이 값을 써야 함)
     */
    private long placeOrderReturningOrderId() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder()
                    .addressInfo(addressInfo)
                    .build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        List<OrderDetail> orders = new ArrayList<>();
        int orderMaxCount = 10;
        Random random = new Random();

        for (int i = 0; i < orderMaxCount; i++) {
            ProductRequest request = new ProductRequest(
                    ProductCategoryType.FASHION,
                    "장갑" + i,
                    "따뜻한 장갑",
                    BigDecimal.valueOf(100L * i),
                    (i + 1) * orderMaxCount,
                    1,
                    true,
                    null
            );
            Product product = request.toEntity();
            productRepository.save(product);
            orders.add(new OrderDetail(product.getId(), null, (i + 1) * (random.nextInt(orderMaxCount) + 1)));
        }

        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), orders);

        MvcResult result = mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("orderId").asLong();
    }

    @Test
    @DisplayName("주문 테스트")
    void insertOrderTest() throws Exception {
        placeOrderReturningOrderId();
    }

    @Test
    @DisplayName("주문 목록 List Test")
    void getOrderedListTest() throws Exception {
        // given
        placeOrderReturningOrderId();

        // when & then
        mockMvc.perform(get("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 취소 Test")
    void cancelOrder() throws Exception {
        long orderId = placeOrderReturningOrderId();

        mockMvc.perform(patch("/v1/order/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private long createProductWithOptionVariant(int variantStock) throws Exception {
        ProductRequest productRequest = new ProductRequest(
                ProductCategoryType.FASHION,
                "옵션 티셔츠",
                "옵션 상품 테스트",
                BigDecimal.valueOf(10000L),
                null,
                1,
                true,
                null
        );
        Product product = productRepository.save(productRequest.toEntity());

        ProductOptionRegisterRequest optionRequest = new ProductOptionRegisterRequest(
                List.of(new ProductOptionAxisRequest("사이즈", 0, List.of("M"))),
                List.of(new ProductOptionVariantRequest("SKU-M", variantStock, BigDecimal.valueOf(500), null,
                        List.of(new ProductOptionVariantValueRequest("사이즈", "M"))))
        );

        MvcResult result = mockMvc.perform(post("/v1/product/{productId}/options", product.getId())
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(optionRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long variantId = root.get("data").get("variants").get(0).get("productOptionVariantId").asLong();
        return variantId;
    }

    @Test
    @DisplayName("옵션 조합 주문 성공 - 재고가 옵션 조합에서 차감된다")
    void placeOrderWithOptionVariantTest() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder().addressInfo(addressInfo).build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        long variantId = createProductWithOptionVariant(10);

        Product product = productOptionVariantRepository.findById(variantId).orElseThrow().getProduct();
        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 3);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        ProductOptionVariant variant = productOptionVariantRepository.findById(variantId).orElseThrow();
        assertThat(variant.getStockQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("주문 목록 조회 - 옵션 값 라벨(사이즈: M)이 간략 정보로 포함된다")
    void getOrderedListWithOptionVariantTest() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder().addressInfo(addressInfo).build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        long variantId = createProductWithOptionVariant(10);
        Product product = productOptionVariantRepository.findById(variantId).orElseThrow().getProduct();

        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 1);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].orderedDetail[0].orderedProductResponse.quantity").value(1))
                .andExpect(jsonPath("$.data.content[0].orderedDetail[0].orderedProductDetailResponse.selectedOptions[0].optionName").value("사이즈"))
                .andExpect(jsonPath("$.data.content[0].orderedDetail[0].orderedProductDetailResponse.selectedOptions[0].value").value("M"));
    }

    @Test
    @DisplayName("옵션 조합 주문 실패 - 옵션 상품인데 옵션 조합 미지정")
    void placeOrderOptionProductWithoutVariantIdTest() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder().addressInfo(addressInfo).build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        long variantId = createProductWithOptionVariant(10);
        Product product = productOptionVariantRepository.findById(variantId).orElseThrow().getProduct();

        OrderDetail orderDetail = new OrderDetail(product.getId(), null, 1);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("옵션 조합 주문 실패 - 옵션 없는 상품에 옵션 조합 지정")
    void placeOrderNoOptionProductWithVariantIdTest() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder().addressInfo(addressInfo).build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        long variantId = createProductWithOptionVariant(10);

        ProductRequest noOptionProductRequest = new ProductRequest(
                ProductCategoryType.FASHION, "일반 상품", "옵션 없음",
                BigDecimal.valueOf(5000L), 50, 1, true, null
        );
        Product noOptionProduct = productRepository.save(noOptionProductRequest.toEntity());

        OrderDetail orderDetail = new OrderDetail(noOptionProduct.getId(), variantId, 1);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("옵션 조합 주문 취소 - 취소 시 옵션 조합 재고가 복구된다")
    void cancelOrderWithOptionVariantRestoresStockTest() throws Exception {
        User saveUser = userRepository.findByEmail("test@email.com").orElseThrow();
        if (saveUser.getAddressList().isEmpty()) {
            AddressInfo addressInfo = AddressInfo.builder()
                    .addressType(AddressType.REGION_ADDR)
                    .address("서울시")
                    .extraAddress("3동")
                    .zipCode("12253")
                    .build();
            Address address = Address.builder().addressInfo(addressInfo).build();
            saveUser.addAddress(address);
            saveUser = userRepository.save(saveUser);
        }

        long variantId = createProductWithOptionVariant(10);
        Product product = productOptionVariantRepository.findById(variantId).orElseThrow().getProduct();

        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 4);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        MvcResult result = mockMvc.perform(post("/v1/order")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.get("data").get("orderId").asLong();

        assertThat(productOptionVariantRepository.findById(variantId).orElseThrow().getStockQuantity()).isEqualTo(6);

        mockMvc.perform(patch("/v1/order/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productOptionVariantRepository.findById(variantId).orElseThrow().getStockQuantity()).isEqualTo(10);
    }
}