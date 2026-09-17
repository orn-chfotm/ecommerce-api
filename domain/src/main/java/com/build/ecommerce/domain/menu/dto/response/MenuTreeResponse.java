package com.build.ecommerce.domain.menu.dto.response;

import com.build.ecommerce.domain.menu.entity.MenuDetail;
import com.build.ecommerce.domain.menu.entity.MenuGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record MenuTreeResponse(
        @Schema(name = "메뉴 id")
        Long id,
        @Schema(name = "메뉴 명")
        String name,
        @Schema(name = "메뉴 URL")
        String url,
        @Schema(name = "하위 메뉴 목록")
        List<MenuTreeResponse> children
) {
    public static MenuTreeResponse toTreeDto(MenuGroup group, List<MenuTreeResponse> children) {
        return MenuTreeResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .url(group.getUrl())
                .children(children)
                .build();
    }

    public static List<MenuTreeResponse> toDetailListDto(List<MenuDetail> flatList) {
        Map<Long, List<MenuDetail>> childrenByParentId = flatList.stream()
                .filter(menuDetail -> menuDetail.getParent() != null)
                .collect(Collectors.groupingBy(menuDetail -> menuDetail.getParent().getId()));

        return flatList.stream()
                .filter(menuDetail -> menuDetail.getParent() == null)
                .sorted(Comparator.comparingInt(MenuDetail::getSortOrder))
                .map(root -> toDetailDto(root, childrenByParentId))
                .toList();
    }

    private static MenuTreeResponse toDetailDto(MenuDetail node, Map<Long, List<MenuDetail>> childrenByParentId) {
        List<MenuTreeResponse> children = childrenByParentId.getOrDefault(node.getId(), List.of()).stream()
                .sorted(Comparator.comparingInt(MenuDetail::getSortOrder))
                .map(child -> toDetailDto(child, childrenByParentId))
                .toList();

        return MenuTreeResponse.builder()
                .id(node.getId())
                .name(node.getName())
                .url(node.getUrl())
                .children(children)
                .build();
    }
}
