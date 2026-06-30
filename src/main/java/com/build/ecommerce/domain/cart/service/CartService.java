package com.build.ecommerce.domain.cart.service;

import com.build.ecommerce.domain.cart.dto.request.CartRequest;
import com.build.ecommerce.domain.cart.dto.request.CartUpdateRequest;
import com.build.ecommerce.domain.cart.dto.response.CartResponse;
import com.build.ecommerce.domain.cart.entity.Cart;
import com.build.ecommerce.domain.cart.exception.CartNotFoundException;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.domain.product.exception.ProductNotEnoughStockException;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.infra.persistence.cart.CartRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import com.build.ecommerce.infra.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartResponse addCart(Long userId, CartRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(ProductNotFoundException::new);

        Cart cart = cartRepository.findByUserIdAndProductId(userId, request.productId())
                .orElseGet(() -> Cart.builder()
                        .user(findUser)
                        .product(findProduct)
                        .quantity(0)
                        .build());

        cart.addQuantity(request.quantity());
        validateStock(findProduct, cart.getQuantity());

        return CartResponse.toDto(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCarts(Long userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(CartResponse::toDto)
                .toList();
    }

    public CartResponse updateCart(Long userId, Long cartId, CartUpdateRequest request) {
        Cart findCart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(CartNotFoundException::new);

        validateStock(findCart.getProduct(), request.quantity());
        findCart.updateQuantity(request.quantity());

        return CartResponse.toDto(findCart);
    }

    public void removeCart(Long userId, Long cartId) {
        Cart findCart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(CartNotFoundException::new);
        cartRepository.delete(findCart);
    }

    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStockQuantity() == null) return;
        if (requestedQuantity > product.getStockQuantity()) {
            throw new ProductNotEnoughStockException();
        }
    }
}
