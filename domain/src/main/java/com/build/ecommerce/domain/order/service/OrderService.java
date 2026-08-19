package com.build.ecommerce.domain.order.service;

import com.build.ecommerce.core.exception.type.InvalidInputException;
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
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantValueResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.product.exception.ProductOptionVariantNotFoundException;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.infra.persistence.address.AddressRepository;
import com.build.ecommerce.infra.persistence.order.OrderRepository;
import com.build.ecommerce.infra.persistence.product.ProductOptionVariantRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.infra.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionVariantRepository productOptionVariantRepository;
    private final AddressRepository addressRepository;

    public OrderResponse createOrder(Long userId, OrderRequest request){
        /* 주문자 정보 */
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        /* 주문자 배송지 정보 */
        Address findUserAddr = addressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(AddressNotFoundException::new);

        Order saveOrder = Order.builder()
                .status(OrderStatusType.COMPLETE)
                .user(findUser)
                .addressInfo(findUserAddr.getAddressInfo())
                .totalAmount(BigDecimal.ZERO)
                .build();

        request.orders().forEach((orderDetail) -> {
            Product product = productRepository.findByIdForUpdate(orderDetail.productId())
                    .orElseThrow(ProductNotFoundException::new);

            BigDecimal unitPrice = product.getPrice();
            ProductOptionVariant variant = null;

            if (product.isHasOptions()) {
                if (orderDetail.productOptionVariantId() == null) {
                    throw new InvalidInputException("옵션이 등록된 상품은 옵션 조합을 선택해야 합니다.");
                }

                variant = productOptionVariantRepository.findByIdForUpdate(orderDetail.productOptionVariantId())
                        .orElseThrow(ProductOptionVariantNotFoundException::new);

                if (!variant.getProduct().getId().equals(product.getId())) {
                    throw new InvalidInputException("선택한 옵션 조합이 해당 상품의 옵션이 아닙니다.");
                }

                variant.removeStock(orderDetail.quantity());
                unitPrice = unitPrice.add(variant.getPriceDelta());
            } else {
                if (orderDetail.productOptionVariantId() != null) {
                    throw new InvalidInputException("옵션이 없는 상품에는 옵션 조합을 지정할 수 없습니다.");
                }

                product.removeStock(orderDetail.quantity());
            }

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(orderDetail.quantity()));

            saveOrder.addOrderProduct(OrderProduct.builder()
                    .product(product)
                    .productOptionVariant(variant)
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
        findOrder.getOrderProducts().forEach(op -> {
            if (op.getProductOptionVariant() != null) {
                op.getProductOptionVariant().addStock(op.getQuantity());
            } else {
                op.getProduct().addStock(op.getQuantity());
            }
        });

        return OrderResponse.toDto(findOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderDetails(Long userId, Pageable pageable) {
        Page<Long> idPage = orderRepository.findIdsByUserId(userId, pageable);
        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
        }

        List<Order> orders = orderRepository.findAllDetailsByIds(idPage.getContent());
        Map<Long, Order> orderById = orders.stream()
                .collect(Collectors.toMap(Order::getId, Function.identity()));

        List<Long> variantIds = orders.stream()
                .flatMap(order -> order.getOrderProducts().stream())
                .map(OrderProduct::getProductOptionVariant)
                .filter(variant -> variant != null)
                .map(ProductOptionVariant::getId)
                .distinct()
                .toList();

        Map<Long, List<ProductOptionVariantValueResponse>> selectedOptionsByVariantId = variantIds.isEmpty()
                ? Map.of()
                : productOptionVariantRepository.findVariantValuesByVariantIds(variantIds).stream()
                        .collect(Collectors.groupingBy(
                                variantValue -> variantValue.getProductOptionVariant().getId(),
                                Collectors.mapping(ProductOptionVariantValueResponse::toDto, Collectors.toList())
                        ));

        List<OrderResponse> content = idPage.getContent().stream()
                .map(orderById::get)
                .map(order -> toOrderListResponse(order, selectedOptionsByVariantId))
                .toList();

        return new PageImpl<>(content, pageable, idPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findDetailByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);

        return toOrderDetailResponse(order);
    }

    /**
     * 목록 조회용 - 옵션 값 라벨(예: 사이즈: M, 컬러: 블루)은 포함하되, 배치 조회로 미리 구한
     * selectedOptionsByVariantId에서 꺼내 쓴다. (variant.getProductOptionVariantValues() 직접 접근 금지 - N+1)
     */
    private OrderResponse toOrderListResponse(Order order, Map<Long, List<ProductOptionVariantValueResponse>> selectedOptionsByVariantId) {
        List<OrderedDetail> orderedDetails = order.getOrderProducts().stream()
                .map(orderProduct -> {
                    ProductOptionVariant variant = orderProduct.getProductOptionVariant();
                    List<ProductOptionVariantValueResponse> selectedOptions = variant == null
                            ? null
                            : selectedOptionsByVariantId.getOrDefault(variant.getId(), List.of());

                    return OrderedDetail.toDto(
                            OrderedProductResponse.toDto(orderProduct),
                            OrderedProductDetailResponse.toDto(orderProduct, selectedOptions)
                    );
                })
                .toList();

        return OrderResponse.toOrderedDetailDto(order, orderedDetails);
    }

    /**
     * 단건 상세 조회용 - 주문 한 건만 대상이라 옵션 값 목록을 엔티티에서 바로 lazy loading 해도 무방하다.
     */
    private OrderResponse toOrderDetailResponse(Order order) {
        List<OrderedDetail> orderedDetails = order.getOrderProducts().stream()
                .map(orderProduct -> OrderedDetail.toDto(
                        OrderedProductResponse.toDto(orderProduct),
                        OrderedProductDetailResponse.toDetailDto(orderProduct)
                ))
                .toList();

        return OrderResponse.toOrderedDetailDto(order, orderedDetails);
    }

}
