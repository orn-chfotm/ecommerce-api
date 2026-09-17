package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.build.ecommerce.domain.code.repository.CodeGroupRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.code.enetity.QCodeGroup.codeGroup;

@Repository
@RequiredArgsConstructor
class CodeGroupRepositoryAdapter implements CodeGroupRepository {
    private final CodeGroupJpaRepository repository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CodeGroup registerCodeGroup(CodeGroup codeGroup) {
        return repository.save(codeGroup);
    }

    @Override
    public List<CodeGroup> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CodeGroup> getCodeGroupDetail(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Integer> findMaxSortOrder() {
        return Optional.ofNullable(
                jpaQueryFactory.select(codeGroup.sortOrder.max())
                        .from(codeGroup)
                        .fetchOne()
        );
    }

    @Override
    public void shiftSortOrderUp(int fromInclusive, int toExclusive) {
        jpaQueryFactory.update(codeGroup)
                .set(codeGroup.sortOrder, codeGroup.sortOrder.add(1))
                .where(
                        codeGroup.sortOrder.goe(fromInclusive),
                        codeGroup.sortOrder.lt(toExclusive)
                )
                .execute();
    }

    @Override
    public void shiftSortOrderDown(int fromExclusive, int toInclusive) {
        jpaQueryFactory.update(codeGroup)
                .set(codeGroup.sortOrder, codeGroup.sortOrder.subtract(1))
                .where(
                        codeGroup.sortOrder.gt(fromExclusive),
                        codeGroup.sortOrder.loe(toInclusive)
                )
                .execute();
    }

    @Override
    public void deleteCodeGroup(CodeGroup codeGroup) {
        repository.delete(codeGroup);
    }
}
