package com.build.ecommerce.domain.order.controller;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.entity.AddressInfo;
import com.build.ecommerce.domain.address.enums.AddressType;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductCategoryType;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.repository.UserRepository;
import com.build.ecommerce.helper.UnitTestHelper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class OrderControllerTest extends UnitTestHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

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
        for (int i = 0; i < 10; i++) {
            Product product = ProductRequest.toEntity(new ProductRequest(
                            ProductCategoryType.FASHION,
                            "장갑" + i,
                            "따뜻한 장갑",
                            BigDecimal.valueOf(100L * i),
                            10 * i,
                            1,
                            true,
                            null
                    )
            );
            productRepository.save(product);
            orders.add(new OrderDetail(product.getId(), i * 10));
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
}