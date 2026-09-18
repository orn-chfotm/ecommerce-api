package com.build.ecommerce.userapi.order.controller;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.build.ecommerce.domain.order.dto.request.OrderAddOnDetail;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.order.entity.Order;
import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.enums.OrderStatusType;
import com.build.ecommerce.domain.order.repository.OrderRepository;
import com.build.ecommerce.domain.product.dto.request.ProductAddOnRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionGroupRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionRegisterRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantRequest;
import com.build.ecommerce.domain.product.dto.request.ProductOptionVariantValueRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductUpdateRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductStatusType;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.product.service.ProductAddOnService;
import com.build.ecommerce.domain.product.service.ProductOptionService;
import com.build.ecommerce.domain.product.service.ProductService;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.product.repository.ProductOptionVariantRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.user.repository.UserRepository;
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

    @Autowired
    private ProductOptionService productOptionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAddOnService productAddOnService;

    @Autowired
    private OrderRepository orderRepository;

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
                    null,
                    null,
                    null
            );
            Product product = request.toEntity();
            productRepository.save(product);
            orders.add(new OrderDetail(product.getId(), null, (i + 1) * (random.nextInt(orderMaxCount) + 1), null));
        }

        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), orders);

        MvcResult result = mockMvc.perform(post("/v1/orders")
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
        mockMvc.perform(get("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 취소 Test")
    void cancelOrder() throws Exception {
        long orderId = placeOrderReturningOrderId();

        mockMvc.perform(patch("/v1/orders/" + orderId)
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
                null,
                null,
                null
        );
        Product product = productRepository.save(productRequest.toEntity());

        ProductOptionRegisterRequest optionRequest = new ProductOptionRegisterRequest(
                List.of(new ProductOptionGroupRequest("사이즈", 0, List.of("M"))),
                List.of(new ProductOptionVariantRequest("SKU-M", variantStock, BigDecimal.valueOf(500), null,
                        List.of(new ProductOptionVariantValueRequest("사이즈", "M"))))
        );

        return productOptionService.registerProductOptions(product.getId(), optionRequest)
                .variants().get(0).productOptionVariantId();
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
        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 3, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/orders")
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

        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 1, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/orders")
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

        OrderDetail orderDetail = new OrderDetail(product.getId(), null, 1, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/orders")
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
                BigDecimal.valueOf(5000L), 50, 1, true, null, null, null
        );
        Product noOptionProduct = productRepository.save(noOptionProductRequest.toEntity());

        OrderDetail orderDetail = new OrderDetail(noOptionProduct.getId(), variantId, 1, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        mockMvc.perform(post("/v1/orders")
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

        OrderDetail orderDetail = new OrderDetail(product.getId(), variantId, 4, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.get("data").get("orderId").asLong();

        assertThat(productOptionVariantRepository.findById(variantId).orElseThrow().getStockQuantity()).isEqualTo(6);

        mockMvc.perform(patch("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productOptionVariantRepository.findById(variantId).orElseThrow().getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("주문 이후 상품 정보가 변경돼도 주문 상세는 주문 시점 스냅샷 값을 유지한다")
    void orderDetailKeepsProductSnapshotAfterProductUpdatedTest() throws Exception {
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

        ProductRequest productRequest = new ProductRequest(
                ProductCategoryType.FASHION,
                "스냅샷 원본 상품",
                "주문 시점 설명",
                BigDecimal.valueOf(10000L),
                50,
                1,
                true,
                null,
                null,
                null
        );
        Product product = productRepository.save(productRequest.toEntity());

        OrderDetail orderDetail = new OrderDetail(product.getId(), null, 1, null);
        OrderRequest request = new OrderRequest(saveUser.getAddressList().get(0).getId(), List.of(orderDetail));

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.get("data").get("orderId").asLong();

        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                ProductCategoryType.DIGITAL,
                "변경된 상품명",
                "변경된 설명",
                BigDecimal.valueOf(99999L),
                10,
                1,
                true,
                null,
                null
        );
        productService.updateProductDetail(product.getId(), updateRequest);

        mockMvc.perform(get("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.category").value(ProductCategoryType.FASHION.name()))
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.name").value("스냅샷 원본 상품"))
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.description").value("주문 시점 설명"))
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.price").value(10000));
    }

    @Test
    @DisplayName("스냅샷이 없는 레거시 주문 상품은 라이브 상품 값으로 폴백해 조회된다")
    void orderDetailFallsBackToLiveProductWhenSnapshotMissingTest() throws Exception {
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

        ProductRequest productRequest = new ProductRequest(
                ProductCategoryType.FASHION,
                "레거시 상품",
                "레거시 설명",
                BigDecimal.valueOf(5000L),
                20,
                1,
                true,
                null,
                null,
                null
        );
        Product product = productRepository.save(productRequest.toEntity());

        Order legacyOrder = Order.builder()
                .status(OrderStatusType.COMPLETE)
                .user(saveUser)
                .addressInfo(saveUser.getAddressList().get(0).getAddressInfo())
                .totalAmount(BigDecimal.valueOf(5000L))
                .build();
        legacyOrder.addOrderProduct(OrderProduct.builder()
                .product(product)
                .productOptionVariant(null)
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(5000L))
                .build());
        Order savedOrder = orderRepository.save(legacyOrder);

        mockMvc.perform(get("/v1/orders/" + savedOrder.getId())
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.category").value(ProductCategoryType.FASHION.name()))
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.name").value("레거시 상품"))
                .andExpect(jsonPath("$.data.orderedDetail[0].orderedProductDetailResponse.price").value(5000));
    }

    /* ===== 추가구성상품(ADD_ON) 주문 ===== */

    private long addOnTestAddressId() {
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
        return saveUser.getAddressList().get(0).getId();
    }

    private Product createProduct(String name, ProductType productType, BigDecimal price, int stockQuantity) {
        return productRepository.save(new ProductRequest(
                ProductCategoryType.FASHION,
                name,
                "추가구성상품 주문 테스트용 상품",
                price,
                stockQuantity,
                1,
                true,
                productType,
                null,
                null
        ).toEntity());
    }

    @Test
    @DisplayName("추가구성상품 주문 실패 - 단독으로 주문할 수 없다")
    void placeOrderAddOnProductAloneTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product addOnProduct = createProduct("단독주문 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);

        OrderRequest request = new OrderRequest(addressId,
                List.of(new OrderDetail(addOnProduct.getId(), null, 1, null)));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.ADD_ON_PRODUCT_NOT_ORDERABLE_ALONE.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 주문 실패 - 본품에 매핑되지 않은 추가구성상품")
    void placeOrderAddOnNotMappedTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("미매핑 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("미매핑 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);

        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 1, List.of(new OrderAddOnDetail(addOnProduct.getId(), 1)))
        ));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ProductExceptionCode.ADD_ON_NOT_MAPPED.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 주문 실패 - 같은 본품에 같은 추가구성상품을 중복 선택")
    void placeOrderAddOnDuplicatedTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("중복선택 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("중복선택 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(addOnProduct.getId()));

        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 1, List.of(
                        new OrderAddOnDetail(addOnProduct.getId(), 1),
                        new OrderAddOnDetail(addOnProduct.getId(), 2)
                ))
        ));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ProductExceptionCode.ADD_ON_DUPLICATED.getMessage()));
    }

    @Test
    @DisplayName("추가구성상품 포함 주문 성공 - 총액은 본품 라인과 추가구성 라인의 합이고 각 재고가 독립 차감된다")
    void placeOrderWithAddOnTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("애드온 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("애드온 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(addOnProduct.getId()));

        // 본품 2개, 추가구성 5개 → 추가구성 수량은 본품 수량과 독립이다.
        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 2, List.of(new OrderAddOnDetail(addOnProduct.getId(), 5)))
        ));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                // 10000 * 2 + 3000 * 5 = 35000
                .andExpect(jsonPath("$.data.totalAmount").value(35000));

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(48);
        assertThat(productRepository.findById(addOnProduct.getId()).orElseThrow().getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("추가구성상품 포함 주문 상세 조회 - 추가구성 라인의 parentOrderProductId가 본품 라인 PK와 일치한다")
    void getOrderDetailWithAddOnParentTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("부모참조 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("부모참조 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(addOnProduct.getId()));

        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 1, List.of(new OrderAddOnDetail(addOnProduct.getId(), 2)))
        ));

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        long orderId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("orderId").asLong();

        MvcResult detailResult = mockMvc.perform(get("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode orderedDetail = objectMapper.readTree(detailResult.getResponse().getContentAsString())
                .get("data").get("orderedDetail");
        assertThat(orderedDetail.size()).isEqualTo(2);

        Long mainOrderProductId = null;
        Long addOnParentOrderProductId = null;
        for (JsonNode node : orderedDetail) {
            JsonNode orderedProduct = node.get("orderedProductResponse");
            if (orderedProduct.hasNonNull("parentOrderProductId")) {
                addOnParentOrderProductId = orderedProduct.get("parentOrderProductId").asLong();
            } else {
                mainOrderProductId = orderedProduct.get("orderProductId").asLong();
            }
        }

        assertThat(mainOrderProductId).isNotNull();
        assertThat(addOnParentOrderProductId).isEqualTo(mainOrderProductId);
    }

    @Test
    @DisplayName("추가구성상품 포함 주문 취소 - 본품과 추가구성상품 재고가 모두 복원된다")
    void cancelOrderWithAddOnRestoresStockTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("취소 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("취소 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(addOnProduct.getId()));

        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 3, List.of(new OrderAddOnDetail(addOnProduct.getId(), 4)))
        ));

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        long orderId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("orderId").asLong();

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(47);
        assertThat(productRepository.findById(addOnProduct.getId()).orElseThrow().getStockQuantity()).isEqualTo(16);

        mockMvc.perform(patch("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(50);
        assertThat(productRepository.findById(addOnProduct.getId()).orElseThrow().getStockQuantity()).isEqualTo(20);
    }

    /* ===== 주문 가능 상태(isOrderable) 게이트 ===== */

    /**
     * 저장된 상품의 판매 상태를 변경한다. (주문 가능 여부 게이트 검증용)
     */
    private void changeProductStatus(Long productId, ProductStatusType statusType) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.changeStatus(statusType);
        productRepository.save(product);
    }

    @Test
    @DisplayName("주문 실패 - 매진(SOLD_OUT) 상품은 주문할 수 없다")
    void placeOrderSoldOutProductTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("매진 상품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        changeProductStatus(product.getId(), ProductStatusType.SOLD_OUT);

        OrderRequest request = new OrderRequest(addressId,
                List.of(new OrderDetail(product.getId(), null, 1, null)));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.PRODUCT_NOT_ORDERABLE.getMessage()));
    }

    @Test
    @DisplayName("주문 실패 - 판매중지(STOPPED) 상품은 주문할 수 없다")
    void placeOrderStoppedProductTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("판매중지 상품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        changeProductStatus(product.getId(), ProductStatusType.STOPPED);

        OrderRequest request = new OrderRequest(addressId,
                List.of(new OrderDetail(product.getId(), null, 1, null)));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.PRODUCT_NOT_ORDERABLE.getMessage()));
    }

    @Test
    @DisplayName("주문 실패 - 추가구성상품 라인이 매진 상태면 주문할 수 없다")
    void placeOrderWithSoldOutAddOnTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("매진애드온 본품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        Product addOnProduct = createProduct("매진 세탁망", ProductType.ADD_ON, BigDecimal.valueOf(3000), 20);
        productAddOnService.registerAddOn(product.getId(), new ProductAddOnRequest(addOnProduct.getId()));
        changeProductStatus(addOnProduct.getId(), ProductStatusType.SOLD_OUT);

        OrderRequest request = new OrderRequest(addressId, List.of(
                new OrderDetail(product.getId(), null, 1, List.of(new OrderAddOnDetail(addOnProduct.getId(), 1)))
        ));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ProductExceptionCode.PRODUCT_NOT_ORDERABLE.getMessage()));
    }

    @Test
    @DisplayName("주문 성공 - status가 null인 상품은 정상 주문된다")
    void placeOrderNullStatusProductTest() throws Exception {
        long addressId = addOnTestAddressId();
        // 신규 등록 상품은 status가 null이며, null은 "상태 미지정 = 주문 가능"을 의미한다.
        Product product = createProduct("상태 미지정 상품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStatus()).isNull();

        OrderRequest request = new OrderRequest(addressId,
                List.of(new OrderDetail(product.getId(), null, 2, null)));

        mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(20000));

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(48);
    }

    @Test
    @DisplayName("삭제된 상품이 포함된 과거 주문도 조회·취소가 정상 동작한다")
    void getAndCancelOrderWithDeletedProductTest() throws Exception {
        long addressId = addOnTestAddressId();
        Product product = createProduct("삭제될 상품", ProductType.NORMAL, BigDecimal.valueOf(10000), 50);

        OrderRequest request = new OrderRequest(addressId,
                List.of(new OrderDetail(product.getId(), null, 3, null)));

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        long orderId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("orderId").asLong();

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(47);

        // 주문 이후 상품을 삭제(soft delete)해도 과거 주문 조회/취소는 영향을 받지 않아야 한다.
        productService.deleteProduct(product.getId());

        mockMvc.perform(get("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(patch("/v1/orders/" + orderId)
                        .headers(getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStockQuantity()).isEqualTo(50);
    }
}