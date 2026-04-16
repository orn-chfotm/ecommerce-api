package com.build.ecommerce.domain.order.service;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.exception.AddressNotFoundException;
import com.build.ecommerce.domain.address.repository.AddressRepository;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.order.dto.response.OrderResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedDetail;
import com.build.ecommerce.domain.order.dto.response.OrderedProductDetailResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedProductResponse;
import com.build.ecommerce.domain.order.entity.Order;
import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.enums.OrderStatus;
import com.build.ecommerce.domain.order.exception.OrderNotFoundException;
import com.build.ecommerce.domain.order.repository.OrderRepository;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderResponse createOrder(Long userId, OrderRequest request){
        /* 주문자 정보 */
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        /* 주문자 배송지 정보 */
        Address findUserAddr = addressRepository.findById(request.addressId())
                .orElseThrow(AddressNotFoundException::new);

        List<Long> productIdList = request.orders().stream()
                .map(OrderDetail::productId)
                .toList();
        List<Product> productList = productRepository.findAllById(productIdList);

        Order saveOrder = Order.builder()
                .status(OrderStatus.COMPLETE)
                .user(findUser)
                .addressInfo(findUserAddr.getAddressInfo())
                .build();

        request.orders().stream().forEach((orderDetail) -> {
            // 주문 제품 검증
            Product product = productList.stream()
                    .filter(p -> p.getId().equals(orderDetail.productId()))
                    .findFirst()
                    .orElseThrow(ProductNotFoundException::new);

            // 수량 감수
            product.removeStock(orderDetail.quantity());

            /*  제품 가격 * 주문 수량 */
            BigDecimal multiply = product.getPrice().multiply(BigDecimal.valueOf(orderDetail.quantity()));

            saveOrder.addOrderProduct(OrderProduct.builder()
                    .product(product)
                    .quantity(orderDetail.quantity())
                    .totalPrice(multiply)
                    .build());
        });

        orderRepository.save(saveOrder);
        return OrderResponse.toDto(saveOrder);
    }

    public OrderResponse cancelOrder(Long orderId) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        findOrder.cancel();

        return OrderResponse.toDto(findOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return user.getOrders().stream()
                .map(order -> {
                    List<OrderedDetail> orderedDetails = order.getOrderProducts().stream()
                            .map(orderProduct -> OrderedDetail.toDto(
                                            OrderedProductResponse.toDto(orderProduct),
                                            OrderedProductDetailResponse.toDto(orderProduct.getProduct())
                                    )
                            ).toList();
                    return OrderResponse.toOrderedDetailDto(order, orderedDetails);
                }).toList();
    }

}
