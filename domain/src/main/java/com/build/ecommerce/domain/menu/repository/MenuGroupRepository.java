package com.build.ecommerce.domain.menu.repository;

import com.build.ecommerce.domain.menu.entity.MenuGroup;

import java.util.List;
import java.util.Optional;

public interface MenuGroupRepository {

    MenuGroup registerMenuGroup(MenuGroup menuGroup);

    List<MenuGroup> findAll();

    Optional<MenuGroup> getMenuGroupDetail(Long id);

    Optional<Integer> findMaxSortOrder();

    void shiftSortOrderUp(int fromInclusive, int toExclusive);

    void shiftSortOrderDown(int fromExclusive, int toInclusive);

    void deleteMenuGroup(MenuGroup menuGroup);
}
