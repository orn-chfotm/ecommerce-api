package com.build.ecommerce.infra.persistence.order;

import com.build.ecommerce.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.order.entity.QOrder.order;
import static com.build.ecommerce.domain.order.entity.QOrderProduct.orderProduct;

@RequiredArgsConstructor
class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 컬렉션 fetch 없이 order id만 페이지네이션한다.
     * (컬렉션 fetch join + Pageable을 같이 쓰면 Hibernate가 페이지네이션을 메모리에서 처리하거나
     * MultipleBagFetchException을 던질 수 있어 분리한다.)
     */
    @Override
    public Page<Long> findIdsByUserId(Long userId, Pageable pageable) {
        List<Long> content = jpaQueryFactory.select(order.id)
                .from(order)
                .where(order.user.id.eq(userId))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(order.count())
                .from(order)
                .where(order.user.id.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * id 목록에 대해서만 상세(orderProducts, product, productOptionVariant)를 일괄 fetch join 한다.
     * 페이지네이션이 없는 상태에서 조회하므로 컬렉션 fetch와 함께 사용해도 안전하다.
     */
    @Override
    public List<Order> findAllDetailsByIds(List<Long> ids) {
        return jpaQueryFactory.selectFrom(order)
                .distinct()
                .leftJoin(order.orderProducts, orderProduct).fetchJoin()
                .leftJoin(orderProduct.product).fetchJoin()
                .leftJoin(orderProduct.productOptionVariant).fetchJoin()
                .where(order.id.in(ids))
                .fetch();
    }

    @Override
    public Optional<Order> findDetailByIdAndUserId(Long orderId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(order)
                        .distinct()
                        .leftJoin(order.orderProducts, orderProduct).fetchJoin()
                        .leftJoin(orderProduct.product).fetchJoin()
                        .leftJoin(orderProduct.productOptionVariant).fetchJoin()
                        .where(
                                order.id.eq(orderId),
                                order.user.id.eq(userId)
                        )
                        .fetchOne()
        );
    }
}
