package com.build.ecommerce.domain.order.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.exception.code.AddressExceptionCode;
import com.build.ecommerce.domain.order.dto.request.OrderAddOnDetail;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import com.build.ecommerce.domain.order.dto.request.OrderRequest;
import com.build.ecommerce.domain.order.dto.response.OrderResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedDetail;
import com.build.ecommerce.domain.order.dto.response.OrderedProductDetailResponse;
import com.build.ecommerce.domain.order.dto.response.OrderedProductResponse;
import com.build.ecommerce.domain.order.entity.Order;
import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.entity.ProductOptionVariantSnapshot;
import com.build.ecommerce.domain.order.entity.ProductSnapshot;
import com.build.ecommerce.domain.order.enums.OrderStatusType;
import com.build.ecommerce.domain.order.exception.code.OrderExceptionCode;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantValueResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.code.UserExceptionCode;
import com.build.ecommerce.domain.address.repository.AddressRepository;
import com.build.ecommerce.domain.order.repository.OrderRepository;
import com.build.ecommerce.domain.product.repository.ProductAddOnRepository;
import com.build.ecommerce.domain.product.repository.ProductOptionVariantRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionVariantRepository productOptionVariantRepository;
    private final ProductAddOnRepository productAddOnRepository;
    private final AddressRepository addressRepository;

    public OrderResponse createOrder(Long userId, OrderRequest request){
        /* 주문자 정보 */
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserExceptionCode.USER_NOT_FOUND));

        /* 주문자 배송지 정보 */
        Address findUserAddr = addressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(() -> new NotFoundException(AddressExceptionCode.ADDRESS_NOT_FOUND));

        Order saveOrder = Order.builder()
                .status(OrderStatusType.COMPLETE)
                .user(findUser)
                .addressInfo(findUserAddr.getAddressInfo())
                .totalAmount(BigDecimal.ZERO)
                .build();

        /*
         * Phase 1 - 락 획득.
         * 요청 배열 순서 그대로 잠그면, 같은 상품 집합을 서로 다른 순서로 주문한 트랜잭션끼리
         * 서로가 가진 행 락을 기다리는 순환 대기(데드락)가 발생한다.
         * 이를 막기 위해 (1) 테이블 순서를 PRODUCTS -> PRODUCT_OPTION_VARIANT로 고정하고,
         * (2) 각 테이블 안에서는 id 오름차순으로 중복 없이 잠근다.
         */
        Map<Long, Product> lockedProducts = new LinkedHashMap<>();
        Map<Long, ProductOptionVariant> lockedVariants = new LinkedHashMap<>();

        try {
            for (Long productId : collectLockTargetProductIds(request.orders())) {
                lockedProducts.put(productId, productRepository.findByIdForUpdate(productId)
                        .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND)));
            }

            for (Long variantId : collectLockTargetVariantIds(request.orders())) {
                lockedVariants.put(variantId, productOptionVariantRepository.findByIdForUpdate(variantId)
                        .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_OPTION_VARIANT_NOT_FOUND)));
            }
        } catch (PessimisticLockingFailureException exception) {
            /* 락 대기 타임아웃 등 락 획득 실패는 도메인 맥락 메시지로 변환해 응답한다. */
            throw new BusinessException(OrderExceptionCode.ORDER_LOCK_ACQUIRE_FAILED);
        }

        /* Phase 2 - 검증/재고 차감/주문 라인 생성. Phase 1에서 잠근 엔티티만 사용한다(재조회 금지). */
        request.orders().forEach((orderDetail) -> {
            Product product = lockedProducts.get(orderDetail.productId());

            /* 추가구성상품은 본품의 하위 라인으로만 주문할 수 있다. */
            if (product.isAddOn()) {
                throw new InvalidInputException(ProductExceptionCode.ADD_ON_PRODUCT_NOT_ORDERABLE_ALONE);
            }

            /* 매진/판매중지/비노출/삭제 상품은 주문 불가(노출은 유지). */
            if (!product.isOrderable()) {
                throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_ORDERABLE);
            }

            BigDecimal unitPrice = product.getPrice();
            ProductOptionVariant variant = null;

            if (product.isHasOptions()) {
                if (orderDetail.productOptionVariantId() == null) {
                    throw new InvalidInputException("옵션이 등록된 상품은 옵션 조합을 선택해야 합니다.");
                }

                variant = lockedVariants.get(orderDetail.productOptionVariantId());

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

            ProductSnapshot productSnapshot = ProductSnapshot.from(product);
            ProductOptionVariantSnapshot variantSnapshot = variant == null ? null : ProductOptionVariantSnapshot.from(variant);

            OrderProduct mainOrderProduct = OrderProduct.builder()
                    .product(product)
                    .productOptionVariant(variant)
                    .productSnapshot(productSnapshot)
                    .productOptionVariantSnapshot(variantSnapshot)
                    .quantity(orderDetail.quantity())
                    .totalPrice(lineTotal)
                    .build();

            /* 본품 라인을 먼저 컬렉션에 담아야 추가구성상품 라인의 부모 FK가 안전하게 채워진다. */
            saveOrder.addOrderProduct(mainOrderProduct);

            addAddOnOrderProducts(saveOrder, mainOrderProduct, product, orderDetail, lockedProducts);
        });

        BigDecimal totalAmount = saveOrder.getOrderProducts().stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saveOrder.updateTotalAmount(totalAmount);

        orderRepository.save(saveOrder);
        return OrderResponse.toDto(saveOrder);
    }

    /**
     * 본품 라인 하위에 추가구성상품 라인을 생성한다.
     * 요청한 추가구성상품이 정말 해당 본품에 등록된 것인지 서버에서 다시 검증한다(임의 id 주입 방어).
     * 추가구성상품 수량은 본품 수량과 독립이며, 가격은 추가구성상품 자신의 판매가를 그대로 사용한다.
     */
    private void addAddOnOrderProducts(Order saveOrder,
                                       OrderProduct parentOrderProduct,
                                       Product product,
                                       OrderDetail orderDetail,
                                       Map<Long, Product> lockedProducts) {
        List<OrderAddOnDetail> addOns = addOnsOf(orderDetail);
        if (addOns.isEmpty()) {
            return;
        }

        List<Long> addOnProductIds = addOns.stream()
                .map(OrderAddOnDetail::productId)
                .toList();

        /* 같은 본품 라인 안에서 동일 추가구성상품을 중복 선택하면 수량 합산 없이 오류로 처리한다. */
        if (addOnProductIds.stream().distinct().count() != addOnProductIds.size()) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_DUPLICATED);
        }

        List<Long> mappedAddOnProductIds = productAddOnRepository
                .findAllByProductIdAndAddOnProductIdIn(product.getId(), addOnProductIds).stream()
                .map(productAddOn -> productAddOn.getAddOnProduct().getId())
                .toList();

        if (!mappedAddOnProductIds.containsAll(addOnProductIds)) {
            throw new InvalidInputException(ProductExceptionCode.ADD_ON_NOT_MAPPED);
        }

        addOns.forEach(addOnDetail -> {
            Product addOnProduct = lockedProducts.get(addOnDetail.productId());

            /* 매진/판매중지/비노출/삭제 상품은 주문 불가(노출은 유지). 추가구성상품도 동일하게 막는다. */
            if (!addOnProduct.isOrderable()) {
                throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_ORDERABLE);
            }

            addOnProduct.removeStock(addOnDetail.quantity());

            BigDecimal addOnLineTotal = addOnProduct.getPrice()
                    .multiply(BigDecimal.valueOf(addOnDetail.quantity()));

            saveOrder.addOrderProduct(OrderProduct.builder()
                    .product(addOnProduct)
                    .productSnapshot(ProductSnapshot.from(addOnProduct))
                    .quantity(addOnDetail.quantity())
                    .totalPrice(addOnLineTotal)
                    .parentOrderProduct(parentOrderProduct)
                    .build());
        });
    }

    /**
     * 잠글 제품 id(본품 + 추가구성상품)를 중복 제거 후 오름차순으로 정렬해 반환한다.
     * 락 획득 순서를 트랜잭션마다 동일하게 맞춰 순환 대기(데드락)를 방지하기 위한 순수 함수다.
     * 추가구성상품은 여러 본품이 공유하는 경합이 잦은 행이므로 반드시 본품과 같은 정렬 순서에 포함해야 한다.
     */
    static List<Long> collectLockTargetProductIds(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .flatMap(orderDetail -> Stream.concat(
                        Stream.of(orderDetail.productId()),
                        addOnsOf(orderDetail).stream().map(OrderAddOnDetail::productId)
                ))
                .filter(productId -> productId != null)
                .distinct()
                .sorted()
                .toList();
    }

    /** addOns는 선택 항목이라 null일 수 있으므로 빈 리스트로 취급한다. */
    private static List<OrderAddOnDetail> addOnsOf(OrderDetail orderDetail) {
        return orderDetail.addOns() == null ? List.of() : orderDetail.addOns();
    }

    /**
     * 잠글 옵션 조합(SKU) id를 중복 제거 후 오름차순으로 정렬해 반환한다.
     * 옵션이 없는 주문 라인은 variantId가 null이므로 제외한다.
     */
    static List<Long> collectLockTargetVariantIds(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(OrderDetail::productOptionVariantId)
                .filter(variantId -> variantId != null)
                .distinct()
                .sorted()
                .toList();
    }

    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order findOrder = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException(OrderExceptionCode.ORDER_NOT_FOUND));

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
                .orElseThrow(() -> new NotFoundException(OrderExceptionCode.ORDER_NOT_FOUND));

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
