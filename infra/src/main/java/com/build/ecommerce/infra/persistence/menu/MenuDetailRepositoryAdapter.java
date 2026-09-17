package com.build.ecommerce.infra.persistence.menu;

import com.build.ecommerce.domain.menu.entity.MenuDetail;
import com.build.ecommerce.domain.menu.repository.MenuDetailRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.menu.entity.QMenuDetail.menuDetail;

@Repository
@RequiredArgsConstructor
class MenuDetailRepositoryAdapter implements MenuDetailRepository {
    private final MenuDetailJpaRepository repository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MenuDetail registerMenuDetail(MenuDetail menuDetail) {
        return repository.save(menuDetail);
    }

    @Override
    public List<MenuDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<MenuDetail> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<MenuDetail> findByIdAndMenuGroup_Id(Long menuGroupId, Long menuDetailId) {
        return repository.findByIdAndMenuGroup_Id(menuDetailId, menuGroupId);
    }

    @Override
    public Optional<Integer> findMaxSortOrder(Long menuGroupId, Long parentId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(menuDetail.sortOrder.max())
                        .from(menuDetail)
                        .where(menuDetail.menuGroup.id.eq(menuGroupId), parentEq(parentId))
                        .fetchOne()
        );
    }

    @Override
    public void shiftSortOrderUp(Long menuGroupId, Long parentId, int fromInclusive, int toExclusive) {
        jpaQueryFactory.update(menuDetail)
                .set(menuDetail.sortOrder, menuDetail.sortOrder.add(1))
                .where(
                        menuDetail.menuGroup.id.eq(menuGroupId),
                        parentEq(parentId),
                        menuDetail.sortOrder.goe(fromInclusive),
                        menuDetail.sortOrder.lt(toExclusive)
                )
                .execute();
    }

    @Override
    public void shiftSortOrderDown(Long menuGroupId, Long parentId, int fromExclusive, int toInclusive) {
        jpaQueryFactory.update(menuDetail)
                .set(menuDetail.sortOrder, menuDetail.sortOrder.subtract(1))
                .where(
                        menuDetail.menuGroup.id.eq(menuGroupId),
                        parentEq(parentId),
                        menuDetail.sortOrder.gt(fromExclusive),
                        menuDetail.sortOrder.loe(toInclusive)
                )
                .execute();
    }

    @Override
    public boolean existsByParentId(Long parentId) {
        return repository.existsByParentId(parentId);
    }

    @Override
    public boolean existsByMenuGroup_Id(Long menuGroupId) {
        return repository.existsByMenuGroup_Id(menuGroupId);
    }

    @Override
    public void deleteMenuDetail(MenuDetail menuDetail) {
        repository.delete(menuDetail);
    }

    private BooleanExpression parentEq(Long parentId) {
        return parentId == null ? menuDetail.parent.isNull() : menuDetail.parent.id.eq(parentId);
    }
}
