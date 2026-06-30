package com.build.ecommerce.domain.order.service;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.exception.AddressNotFoundException;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.order.dto.response.OrderResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedDetail;
import com.build.ecommerce.domain.order.dto.response.OrderedProductDetailResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedProductResponse;
import com.build.ecommerce.domain.order.entity.Order;
import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.enums.OrderStatusType;
import com.build.ecommerce.domain.order.exception.OrderNotFoundException;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.infra.persistence.address.AddressRepository;
import com.build.ecommerce.infra.persistence.order.OrderRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.infra.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        Address findUserAddr = addressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(AddressNotFoundException::new);

        List<Long> productIdList = request.orders().stream()
                .map(OrderDetail::productId)
                .toList();
        List<Product> productList = productRepository.findAllById(productIdList);

        Order saveOrder = Order.builder()
                .status(OrderStatusType.COMPLETE)
                .user(findUser)
                .addressInfo(findUserAddr.getAddressInfo())
                .totalAmount(BigDecimal.ZERO)
                .build();

        request.orders().forEach((orderDetail) -> {
            Product product = productList.stream()
                    .filter(p -> p.getId().equals(orderDetail.productId()))
                    .findFirst()
                    .orElseThrow(ProductNotFoundException::new);

            product.removeStock(orderDetail.quantity());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(orderDetail.quantity()));

            saveOrder.addOrderProduct(OrderProduct.builder()
                    .product(product)
                    .quantity(orderDetail.quantity())
                    .totalPrice(lineTotal)
                    .build());
        });

        BigDecimal totalAmount = saveOrder.getOrderProducts().stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saveOrder.updateTotalAmount(totalAmount);

        orderRepository.save(saveOrder);
        return OrderResponse.toDto(saveOrder);
    }

    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order findOrder = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);

        findOrder.cancel();
        findOrder.getOrderProducts().forEach(op -> op.getProduct().addStock(op.getQuantity()));

        return OrderResponse.toDto(findOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderDetails(Long userId, Pageable pageable) {
        return orderRepository.findAllDetailsByUserId(userId, pageable)
                .map(this::toOrderDetailResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findDetailByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);

        return toOrderDetailResponse(order);
    }

    private OrderResponse toOrderDetailResponse(Order order) {
        List<OrderedDetail> orderedDetails = order.getOrderProducts().stream()
                .map(orderProduct -> OrderedDetail.toDto(
                        OrderedProductResponse.toDto(orderProduct),
                        OrderedProductDetailResponse.toDto(orderProduct.getProduct())
                ))
                .toList();

        return OrderResponse.toOrderedDetailDto(order, orderedDetails);
    }

}
