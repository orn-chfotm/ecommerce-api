package com.build.ecommerce.infra.persistence.cart;

import com.build.ecommerce.domain.cart.entity.Cart;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.build.ecommerce.domain.cart.entity.QCart.cart;

@RequiredArgsConstructor
class CartCustomRepositoryImpl implements CartCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
