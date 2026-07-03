package com.build.ecommerce.infra.persistence.cart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
