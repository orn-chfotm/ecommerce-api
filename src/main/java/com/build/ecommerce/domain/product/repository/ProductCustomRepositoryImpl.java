package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.dto.request.ProductSerchRequest;
import com.build.ecommerce.domain.product.entity.Category;
import com.build.ecommerce.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Product>> customFind(ProductSerchRequest productSerchRequest) {
        return Optional.ofNullable(
                queryFactory.selectFrom(product)
                .where(
                        product.category.in(productSerchRequest.getCategory()),
                        product.name.like("% " + productSerchRequest.name() + " %"),
                        product.price.between(productSerchRequest.minPrice(), productSerchRequest.maxPrice()),
                        product.stockQuantity.gt(productSerchRequest.stockQuantity())
                )
                .fetch()
        );
    }
}
