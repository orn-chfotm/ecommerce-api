package com.build.ecommerce.domain.code.dto.response;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.enetity.CodeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record CodeTreeResponse(
        @Schema(name = "코드 id")
        Long id,
        @Schema(name = "코드")
        String code,
        @Schema(name = "코드 명")
        String name,
        @Schema(name = "하위 코드 목록")
        List<CodeTreeResponse> children
) {
    public static CodeTreeResponse toTreeDto(CodeGroup group, List<CodeTreeResponse> children) {
        return CodeTreeResponse.builder()
                .id(group.getId())
                .code(group.getCode())
                .name(group.getName())
                .children(children)
                .build();
    }

    public static List<CodeTreeResponse> toDetailListDto(List<CodeDetail> flatList) {
        Map<Long, List<CodeDetail>> childrenByParentId = flatList.stream()
                .filter(codeDetail -> codeDetail.getParent() != null)
                .collect(Collectors.groupingBy(codeDetail -> codeDetail.getParent().getId()));

        return flatList.stream()
                .filter(codeDetail -> codeDetail.getParent() == null)
                .sorted(Comparator.comparingInt(CodeDetail::getSortOrder))
                .map(root -> toDetailDto(root, childrenByParentId))
                .toList();
    }

    private static CodeTreeResponse toDetailDto(CodeDetail node, Map<Long, List<CodeDetail>> childrenByParentId) {
        List<CodeTreeResponse> children = childrenByParentId.getOrDefault(node.getId(), List.of()).stream()
                .sorted(Comparator.comparingInt(CodeDetail::getSortOrder))
                .map(child -> toDetailDto(child, childrenByParentId))
                .toList();

        return CodeTreeResponse.builder()
                .id(node.getId())
                .code(node.getCode())
                .name(node.getName())
                .children(children)
                .build();
    }
}
