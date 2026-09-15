package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.repository.CodeDetailRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.code.enetity.QCodeDetail.codeDetail;

@Repository
@RequiredArgsConstructor
class CodeDetailRepositoryAdapter implements CodeDetailRepository {
    private final CodeDetailJpaRepository repository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CodeDetail registerCodeDetail(CodeDetail codeDetail) {
        return repository.save(codeDetail);
    }

    @Override
    public List<CodeDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CodeDetail> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Integer> findMaxSortOrder(Long codeGroupId, Long parentId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(codeDetail.sortOrder.max())
                        .from(codeDetail)
                        .where(codeDetail.codeGroup.id.eq(codeGroupId), parentEq(parentId))
                        .fetchOne()
        );
    }

    @Override
    public void shiftSortOrderUp(Long codeGroupId, Long parentId, int fromInclusive, int toExclusive) {
        jpaQueryFactory.update(codeDetail)
                .set(codeDetail.sortOrder, codeDetail.sortOrder.add(1))
                .where(
                        codeDetail.codeGroup.id.eq(codeGroupId),
                        parentEq(parentId),
                        codeDetail.sortOrder.goe(fromInclusive),
                        codeDetail.sortOrder.lt(toExclusive)
                )
                .execute();
    }

    @Override
    public void shiftSortOrderDown(Long codeGroupId, Long parentId, int fromExclusive, int toInclusive) {
        jpaQueryFactory.update(codeDetail)
                .set(codeDetail.sortOrder, codeDetail.sortOrder.subtract(1))
                .where(
                        codeDetail.codeGroup.id.eq(codeGroupId),
                        parentEq(parentId),
                        codeDetail.sortOrder.gt(fromExclusive),
                        codeDetail.sortOrder.loe(toInclusive)
                )
                .execute();
    }

    private BooleanExpression parentEq(Long parentId) {
        return parentId == null ? codeDetail.parent.isNull() : codeDetail.parent.id.eq(parentId);
    }
}
