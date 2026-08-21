package com.build.ecommerce.infra.persistence.cart;

import com.build.ecommerce.domain.cart.entity.Cart;
import com.build.ecommerce.domain.cart.repository.CartRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.cart.entity.QCart.cart;

@Repository
@RequiredArgsConstructor
class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Cart save(Cart cart) {
        return jpaRepository.save(cart);
    }

    @Override
    public List<Cart> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Cart> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public void delete(Cart cartEntity) {
        jpaRepository.delete(cartEntity);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        jpaQueryFactory.delete(cart)
                .where(cart.user.id.eq(userId))
                .execute();
    }

    @Override
    public Optional<Cart> findByUserIdAndProductIdAndVariantId(Long userId, Long productId, Long variantId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(cart)
                        .where(
                                cart.user.id.eq(userId),
                                cart.product.id.eq(productId),
                                variantEq(variantId)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression variantEq(Long variantId) {
        return variantId == null
                ? cart.productOptionVariant.isNull()
                : cart.productOptionVariant.id.eq(variantId);
    }
}
