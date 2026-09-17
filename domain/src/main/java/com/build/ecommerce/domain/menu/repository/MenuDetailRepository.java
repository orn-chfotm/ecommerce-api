package com.build.ecommerce.domain.menu.repository;

import com.build.ecommerce.domain.menu.entity.MenuDetail;

import java.util.List;
import java.util.Optional;

public interface MenuDetailRepository {

    MenuDetail registerMenuDetail(MenuDetail menuDetail);

    List<MenuDetail> findAll();

    Optional<MenuDetail> findById(Long id);

    Optional<MenuDetail> findByIdAndMenuGroup_Id(Long menuGroupId, Long menuDetailId);

    Optional<Integer> findMaxSortOrder(Long menuGroupId, Long parentId);

    void shiftSortOrderUp(Long menuGroupId, Long parentId, int fromInclusive, int toExclusive);

    void shiftSortOrderDown(Long menuGroupId, Long parentId, int fromExclusive, int toInclusive);

    boolean existsByParentId(Long parentId);

    boolean existsByMenuGroup_Id(Long menuGroupId);

    void deleteMenuDetail(MenuDetail menuDetail);
}
