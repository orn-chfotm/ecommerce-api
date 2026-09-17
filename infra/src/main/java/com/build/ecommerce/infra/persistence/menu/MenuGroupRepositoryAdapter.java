package com.build.ecommerce.infra.persistence.menu;

import com.build.ecommerce.domain.menu.entity.MenuGroup;
import com.build.ecommerce.domain.menu.repository.MenuGroupRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.menu.entity.QMenuGroup.menuGroup;

@Repository
@RequiredArgsConstructor
class MenuGroupRepositoryAdapter implements MenuGroupRepository {
    private final MenuGroupJpaRepository repository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MenuGroup registerMenuGroup(MenuGroup menuGroup) {
        return repository.save(menuGroup);
    }

    @Override
    public List<MenuGroup> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<MenuGroup> getMenuGroupDetail(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Integer> findMaxSortOrder() {
        return Optional.ofNullable(
                jpaQueryFactory.select(menuGroup.sortOrder.max())
                        .from(menuGroup)
                        .fetchOne()
        );
    }

    @Override
    public void shiftSortOrderUp(int fromInclusive, int toExclusive) {
        jpaQueryFactory.update(menuGroup)
                .set(menuGroup.sortOrder, menuGroup.sortOrder.add(1))
                .where(
                        menuGroup.sortOrder.goe(fromInclusive),
                        menuGroup.sortOrder.lt(toExclusive)
                )
                .execute();
    }

    @Override
    public void shiftSortOrderDown(int fromExclusive, int toInclusive) {
        jpaQueryFactory.update(menuGroup)
                .set(menuGroup.sortOrder, menuGroup.sortOrder.subtract(1))
                .where(
                        menuGroup.sortOrder.gt(fromExclusive),
                        menuGroup.sortOrder.loe(toInclusive)
                )
                .execute();
    }

    @Override
    public void deleteMenuGroup(MenuGroup menuGroup) {
        repository.delete(menuGroup);
    }
}
