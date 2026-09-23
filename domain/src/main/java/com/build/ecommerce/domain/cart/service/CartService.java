package com.build.ecommerce.domain.cart.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.cart.dto.request.CartRequest;
import com.build.ecommerce.domain.cart.dto.request.CartUpdateRequest;
import com.build.ecommerce.domain.cart.dto.response.CartResponse;
import com.build.ecommerce.domain.cart.entity.Cart;
import com.build.ecommerce.domain.cart.exception.code.CartExceptionCode;
import com.build.ecommerce.domain.product.dto.response.ProductOptionVariantValueResponse;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.code.UserExceptionCode;
import com.build.ecommerce.domain.cart.repository.CartRepository;
import com.build.ecommerce.domain.product.repository.ProductOptionVariantRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionVariantRepository productOptionVariantRepository;

    public CartResponse addCart(Long userId, CartRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserExceptionCode.USER_NOT_FOUND));
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        ProductOptionVariant findVariant = resolveVariant(findProduct, request.productOptionVariantId());

        Cart cart = cartRepository.findByUserIdAndProductIdAndVariantId(userId, request.productId(), request.productOptionVariantId())
                .orElseGet(() -> Cart.builder()
                        .user(findUser)
                        .product(findProduct)
                        .productOptionVariant(findVariant)
                        .quantity(0)
                        .build());

        cart.addQuantity(request.quantity());
        validateStock(findProduct, findVariant, cart.getQuantity());

        return CartResponse.toDto(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCarts(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        List<Long> variantIds = carts.stream()
                .map(Cart::getProductOptionVariant)
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

        return carts.stream()
                .map(cart -> {
                    ProductOptionVariant variant = cart.getProductOptionVariant();
                    List<ProductOptionVariantValueResponse> selectedOptions = variant == null
                            ? null
                            : selectedOptionsByVariantId.getOrDefault(variant.getId(), List.of());
                    return CartResponse.toDto(cart, selectedOptions);
                })
                .toList();
    }

    public CartResponse updateCart(Long userId, Long cartId, CartUpdateRequest request) {
        Cart findCart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new NotFoundException(CartExceptionCode.CART_NOT_FOUND));

        validateStock(findCart.getProduct(), findCart.getProductOptionVariant(), request.quantity());
        findCart.updateQuantity(request.quantity());

        return CartResponse.toDto(findCart);
    }

    public void removeCart(Long userId, Long cartId) {
        Cart findCart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new NotFoundException(CartExceptionCode.CART_NOT_FOUND));
        cartRepository.delete(findCart);
    }

    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    private ProductOptionVariant resolveVariant(Product product, Long productOptionVariantId) {
        if (product.isHasOptions()) {
            if (productOptionVariantId == null) {
                throw new InvalidInputException(CartExceptionCode.CART_OPTION_REQUIRED, "옵션이 등록된 상품은 옵션 조합을 선택해야 합니다.");
            }

            ProductOptionVariant variant = productOptionVariantRepository.findById(productOptionVariantId)
                    .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_OPTION_VARIANT_NOT_FOUND));

            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new InvalidInputException(CartExceptionCode.CART_OPTION_REQUIRED, "선택한 옵션 조합이 해당 상품의 옵션이 아닙니다.");
            }

            return variant;
        }

        if (productOptionVariantId != null) {
            throw new InvalidInputException(CartExceptionCode.CART_OPTION_REQUIRED, "옵션이 없는 상품에는 옵션 조합을 지정할 수 없습니다.");
        }

        return null;
    }

    private void validateStock(Product product, ProductOptionVariant variant, int requestedQuantity) {
        if (variant != null) {
            if (requestedQuantity > variant.getStockQuantity()) {
                throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_ENOUGH_STOCK);
            }
            return;
        }

        if (product.getStockQuantity() == null) return;
        if (requestedQuantity > product.getStockQuantity()) {
            throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_ENOUGH_STOCK);
        }
    }
}
