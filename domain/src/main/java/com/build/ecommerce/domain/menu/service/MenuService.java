package com.build.ecommerce.domain.menu.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailUpdateRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupUpdateRequest;
import com.build.ecommerce.domain.menu.dto.response.MenuDetailResponse;
import com.build.ecommerce.domain.menu.dto.response.MenuGroupResponse;
import com.build.ecommerce.domain.menu.dto.response.MenuTreeResponse;
import com.build.ecommerce.domain.menu.entity.MenuDetail;
import com.build.ecommerce.domain.menu.entity.MenuGroup;
import com.build.ecommerce.domain.menu.exception.code.MenuExceptionCode;
import com.build.ecommerce.domain.menu.repository.MenuDetailRepository;
import com.build.ecommerce.domain.menu.repository.MenuGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {
    private final MenuGroupRepository menuGroupRepository;
    private final MenuDetailRepository menuDetailRepository;

    public MenuGroupResponse registerMenuGroup(MenuGroupRegisterRequest menuGroupRegisterRequest) {
        MenuGroup menuGroup = menuGroupRepository.registerMenuGroup(menuGroupRegisterRequest.toEntity());
        return MenuGroupResponse.toDto(menuGroup);
    }

    public MenuDetailResponse registerMenuDetail(Long menuGroupId, MenuDetailRegisterRequest menuDetailRegisterRequest) {
        MenuGroup menuGroup = menuGroupRepository.getMenuGroupDetail(menuGroupId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_GROUP_NOT_FOUND));

        MenuDetail parent = null;
        if (menuDetailRegisterRequest.parentId() != null) {
            parent = menuDetailRepository.findById(menuDetailRegisterRequest.parentId())
                    .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));
        }

        int nextSortOrder = menuDetailRepository.findMaxSortOrder(menuGroupId, menuDetailRegisterRequest.parentId())
                .map(max -> max + 1)
                .orElse(1);

        MenuDetail menuDetail = menuDetailRepository.registerMenuDetail(
                menuDetailRegisterRequest.toEntity(menuGroup, parent, nextSortOrder)
        );
        return MenuDetailResponse.toDto(menuDetail);
    }

    @Transactional(readOnly = true)
    public List<MenuTreeResponse> getMenuTree() {
        Map<Long, List<MenuDetail>> detailsByGroupId = menuDetailRepository.findAll().stream()
                .collect(Collectors.groupingBy(menuDetail -> menuDetail.getMenuGroup().getId()));

        return menuGroupRepository.findAll().stream()
                .map(group -> MenuTreeResponse.toTreeDto(
                        group, MenuTreeResponse.toDetailListDto(detailsByGroupId.getOrDefault(group.getId(), List.of()))))
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuGroupResponse getMenuGroupDetail(Long menuGroupId) {
        MenuGroup menuGroup = menuGroupRepository.getMenuGroupDetail(menuGroupId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_GROUP_NOT_FOUND));
        return MenuGroupResponse.toDto(menuGroup);
    }

    @Transactional(readOnly = true)
    public MenuDetailResponse getMenuDetailDetail(Long menuDetailId) {
        MenuDetail menuDetail = menuDetailRepository.findById(menuDetailId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));
        return MenuDetailResponse.toDto(menuDetail);
    }

    public MenuGroupResponse updateMenuGroup(Long menuGroupId, MenuGroupUpdateRequest menuGroupUpdateRequest) {
        MenuGroup menuGroup = menuGroupRepository.getMenuGroupDetail(menuGroupId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_GROUP_NOT_FOUND));
        menuGroup.change(menuGroupUpdateRequest);
        return MenuGroupResponse.toDto(menuGroup);
    }

    public MenuDetailResponse updateMenuDetail(Long menuGroupId, Long menuDetailId, MenuDetailUpdateRequest menuDetailUpdateRequest) {
        MenuDetail menuDetail = menuDetailRepository.findByIdAndMenuGroup_Id(menuGroupId, menuDetailId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));

        menuDetail.update(menuDetailUpdateRequest);
        return MenuDetailResponse.toDto(menuDetail);
    }

    public MenuDetailResponse moveMenuDetail(Long menuGroupId, Long menuDetailId, MenuDetailSortOrderMoveRequest moveRequest) {
        MenuDetail current = menuDetailRepository.findByIdAndMenuGroup_Id(menuGroupId, menuDetailId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));

        Long parentId = current.getParent() == null ? null : current.getParent().getId();
        Long targetParentId = moveRequest.targetParentId();
        int targetSortOrder = moveRequest.targetSortOrder();

        boolean parentChanges = targetParentId != null && !targetParentId.equals(parentId);
        if (parentChanges) {
            return moveMenuDetailToNewParent(menuGroupId, current, parentId, targetParentId, targetSortOrder);
        }
        return moveMenuDetailWithinParent(menuGroupId, current, parentId, targetSortOrder);
    }

    private MenuDetailResponse moveMenuDetailWithinParent(Long menuGroupId, MenuDetail current, Long parentId, int targetSortOrder) {
        int originalSortOrder = current.getSortOrder();

        int maxSortOrder = menuDetailRepository.findMaxSortOrder(menuGroupId, parentId).orElse(1);
        if (targetSortOrder < 1 || targetSortOrder > maxSortOrder) {
            throw new InvalidInputException(MenuExceptionCode.MENU_DETAIL_SORT_ORDER_OUT_OF_RANGE);
        }

        if (targetSortOrder == originalSortOrder) {
            return MenuDetailResponse.toDto(current);
        }

        if (targetSortOrder < originalSortOrder) {
            menuDetailRepository.shiftSortOrderUp(menuGroupId, parentId, targetSortOrder, originalSortOrder);
        } else {
            menuDetailRepository.shiftSortOrderDown(menuGroupId, parentId, originalSortOrder, targetSortOrder);
        }

        current.changeSortOrder(targetSortOrder);
        return MenuDetailResponse.toDto(current);
    }

    private MenuDetailResponse moveMenuDetailToNewParent(
            Long menuGroupId, MenuDetail current, Long originalParentId, Long targetParentId, int targetSortOrder
    ) {
        if (targetParentId.equals(current.getId())) {
            throw new InvalidInputException(MenuExceptionCode.MENU_DETAIL_TARGET_PARENT_SELF_REFERENCE);
        }

        if (menuDetailRepository.existsByParentId(current.getId())) {
            throw new BusinessException(MenuExceptionCode.MENU_DETAIL_HAS_CHILDREN);
        }

        MenuDetail targetParent = menuDetailRepository.findByIdAndMenuGroup_Id(menuGroupId, targetParentId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));

        if (targetParent.getDepth() != 0) {
            throw new InvalidInputException(MenuExceptionCode.MENU_DETAIL_TARGET_PARENT_INVALID_DEPTH);
        }

        int destinationMaxSortOrder = menuDetailRepository.findMaxSortOrder(menuGroupId, targetParentId).orElse(0);
        if (targetSortOrder < 1 || targetSortOrder > destinationMaxSortOrder + 1) {
            throw new InvalidInputException(MenuExceptionCode.MENU_DETAIL_SORT_ORDER_OUT_OF_RANGE);
        }

        menuDetailRepository.shiftSortOrderDown(menuGroupId, originalParentId, current.getSortOrder(), Integer.MAX_VALUE);
        menuDetailRepository.shiftSortOrderUp(menuGroupId, targetParentId, targetSortOrder, Integer.MAX_VALUE);

        current.moveToParent(targetParent, targetSortOrder);
        return MenuDetailResponse.toDto(current);
    }

    public MenuGroupResponse moveMenuGroup(Long menuGroupId, MenuGroupSortOrderMoveRequest moveRequest) {
        MenuGroup menuGroup = menuGroupRepository.getMenuGroupDetail(menuGroupId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_GROUP_NOT_FOUND));

        int originalSortOrder = menuGroup.getSortOrder();
        int targetSortOrder = moveRequest.targetSortOrder();

        int maxSortOrder = menuGroupRepository.findMaxSortOrder().orElse(1);
        if (targetSortOrder < 1 || targetSortOrder > maxSortOrder) {
            throw new InvalidInputException(MenuExceptionCode.MENU_GROUP_SORT_ORDER_OUT_OF_RANGE);
        }

        if (targetSortOrder == originalSortOrder) {
            return MenuGroupResponse.toDto(menuGroup);
        }

        if (targetSortOrder < originalSortOrder) {
            menuGroupRepository.shiftSortOrderUp(targetSortOrder, originalSortOrder);
        } else {
            menuGroupRepository.shiftSortOrderDown(originalSortOrder, targetSortOrder);
        }

        menuGroup.changeSortOrder(targetSortOrder);
        return MenuGroupResponse.toDto(menuGroup);
    }

    public void deleteMenuGroup(Long menuGroupId) {
        MenuGroup menuGroup = menuGroupRepository.getMenuGroupDetail(menuGroupId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_GROUP_NOT_FOUND));

        if (menuDetailRepository.existsByMenuGroup_Id(menuGroupId)) {
            throw new BusinessException(MenuExceptionCode.MENU_GROUP_DELETE_HAS_CHILDREN);
        }

        menuGroupRepository.shiftSortOrderDown(menuGroup.getSortOrder(), Integer.MAX_VALUE);
        menuGroupRepository.deleteMenuGroup(menuGroup);
    }

    public void deleteMenuDetail(Long menuGroupId, Long menuDetailId) {
        MenuDetail menuDetail = menuDetailRepository.findByIdAndMenuGroup_Id(menuGroupId, menuDetailId)
                .orElseThrow(() -> new NotFoundException(MenuExceptionCode.MENU_DETAIL_NOT_FOUND));

        if (menuDetailRepository.existsByParentId(menuDetailId)) {
            throw new BusinessException(MenuExceptionCode.MENU_DETAIL_DELETE_HAS_CHILDREN);
        }

        Long parentId = menuDetail.getParent() == null ? null : menuDetail.getParent().getId();
        menuDetailRepository.shiftSortOrderDown(menuGroupId, parentId, menuDetail.getSortOrder(), Integer.MAX_VALUE);
        menuDetailRepository.deleteMenuDetail(menuDetail);
    }
}
