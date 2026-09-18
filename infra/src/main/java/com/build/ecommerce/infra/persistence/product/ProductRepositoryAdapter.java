package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductSearchScope;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProduct.product;
import static com.build.ecommerce.domain.file.entity.QFileMaster.fileMaster;

@Repository
@RequiredArgsConstructor
class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Product save(Product productEntity) {
        return jpaRepository.save(productEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Product> searchProducts(ProductSearchRequest searchRequest, ProductSearchScope scope, Pageable pageable) {
        // content 쿼리와 count 쿼리가 서로 다른 조건을 쓰면 페이지 총 건수가 어긋나므로 동일 배열을 공유한다.
        BooleanExpression[] conditions = {
                categoryEq(searchRequest.category()),
                nameContains(searchRequest.name()),
                minPriceGoe(searchRequest.minPrice()),
                maxPriceLoe(searchRequest.maxPrice()),
                stockQuantityGoe(searchRequest.stockQuantity()),
                // 사용자 조회는 일반 상품만 노출한다. 관리자 조회는 유형/노출 필터를 적용하지 않는다.
                scope == ProductSearchScope.USER ? product.productType.eq(ProductType.NORMAL) : null,
                scope == ProductSearchScope.USER ? visibleToUser() : null
        };

        // fileMaster는 1:1 관계라 fetch join 해도 페이지네이션 row 수가 늘어나지 않는다.
        // fileDetailList(1:N)는 여기서 fetch join 하지 않고 Service에서 별도 배치 조회한다.
        List<Product> content = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.fileMaster, fileMaster).fetchJoin()
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(product.count())
                .from(product)
                .where(conditions);

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

    // 사용자 노출 게이트. 동일 판정이 Product.isVisibleToUser()에도 있으므로 함께 수정해야 한다.
    // status는 NULL 허용 컬럼이라 ne()/notIn()을 쓰면 NULL 행이 통째로 탈락한다(삼값논리) - 여기서는 status를 보지 않는다.
    // (매진/판매중지 상품도 목록에는 노출되어야 하므로 노출 게이트에 status 조건을 넣지 않는다.)
    private BooleanExpression visibleToUser() {
        return product.delAt.isNull()
                .and(product.active.isTrue())
                .and(product.displayStartAt.isNull().or(product.displayStartAt.loe(LocalDateTime.now())));
    }
}
