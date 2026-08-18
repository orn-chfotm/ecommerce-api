package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProduct.product;
import static com.build.ecommerce.infra.file.entity.QFileMaster.fileMaster;

@RequiredArgsConstructor
class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> searchProducts(ProductSearchRequest searchRequest, Pageable pageable) {
        // fileMaster는 1:1 관계라 fetch join 해도 페이지네이션 row 수가 늘어나지 않는다.
        // fileDetailList(1:N)는 여기서 fetch join 하지 않고 Service에서 별도 배치 조회한다.
        List<Product> content = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.fileMaster, fileMaster).fetchJoin()
                .where(
                        categoryEq(searchRequest.category()),
                        nameContains(searchRequest.name()),
                        minPriceGoe(searchRequest.minPrice()),
                        maxPriceLoe(searchRequest.maxPrice()),
                        stockQuantityGoe(searchRequest.stockQuantity())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(product.count())
                .from(product)
                .where(
                        categoryEq(searchRequest.category()),
                        nameContains(searchRequest.name()),
                        minPriceGoe(searchRequest.minPrice()),
                        maxPriceLoe(searchRequest.maxPrice()),
                        stockQuantityGoe(searchRequest.stockQuantity())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(product)
                        .where(product.id.eq(id))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .fetchOne()
        );
    }

    private BooleanExpression categoryEq(ProductCategoryType category) {
        return category == null
                ? null
                : product.category.eq(category);
    }

    private BooleanExpression nameContains(String name) {
        return name == null || name.isBlank()
                ? null
                : product.name.containsIgnoreCase(name);
    }

    private BooleanExpression minPriceGoe(Integer minPrice) {
        return minPrice == null
                ? null
                : product.price.goe(BigDecimal.valueOf(minPrice));
    }

    private BooleanExpression maxPriceLoe(Integer maxPrice) {
        return maxPrice == null
                ? null
                : product.price.loe(BigDecimal.valueOf(maxPrice));
    }

    private BooleanExpression stockQuantityGoe(Integer stockQuantity) {
        return stockQuantity == null
                ? null
                : product.stockQuantity.goe(stockQuantity);
    }
}
