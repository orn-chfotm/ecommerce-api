package com.build.ecommerce.domain.code.dto.response;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.enetity.CodeGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeTreeResponseTest {

    private CodeGroup group() {
        return CodeGroup.builder()
                .code("GROUP")
                .name("그룹")
                .sortOrder(1)
                .active(true)
                .build();
    }

    private CodeDetail detail(Long id, CodeGroup group, CodeDetail parent, String code, int sortOrder) {
        CodeDetail codeDetail = CodeDetail.builder()
                .codeGroup(group)
                .parent(parent)
                .code(code)
                .name(code + "-이름")
                .sortOrder(sortOrder)
                .active(true)
                .build();
        ReflectionTestUtils.setField(codeDetail, "id", id);
        return codeDetail;
    }

    @Test
    @DisplayName("자식의 자식(3단계)까지 트리로 조립한다")
    void toDetailListDto_buildsMultiLevelTree() {
        CodeGroup group = group();
        CodeDetail root1 = detail(1L, group, null, "A", 1);
        CodeDetail root2 = detail(2L, group, null, "B", 2);
        CodeDetail child1 = detail(3L, group, root1, "A-1", 1);
        CodeDetail grandchild1 = detail(4L, group, child1, "A-1-1", 1);

        List<CodeTreeResponse> tree = CodeTreeResponse.toDetailListDto(
                List.of(grandchild1, root2, child1, root1));

        assertThat(tree).hasSize(2);

        CodeTreeResponse rootNode1 = tree.get(0);
        assertThat(rootNode1.code()).isEqualTo("A");
        assertThat(rootNode1.children()).hasSize(1);

        CodeTreeResponse childNode1 = rootNode1.children().get(0);
        assertThat(childNode1.code()).isEqualTo("A-1");
        assertThat(childNode1.children()).hasSize(1);
        assertThat(childNode1.children().get(0).code()).isEqualTo("A-1-1");
        assertThat(childNode1.children().get(0).children()).isEmpty();

        CodeTreeResponse rootNode2 = tree.get(1);
        assertThat(rootNode2.code()).isEqualTo("B");
        assertThat(rootNode2.children()).isEmpty();
    }

    @Test
    @DisplayName("빈 목록이면 빈 트리를 반환한다")
    void toDetailListDto_returnsEmptyListForEmptyInput() {
        List<CodeTreeResponse> tree = CodeTreeResponse.toDetailListDto(List.of());

        assertThat(tree).isEmpty();
    }

    @Test
    @DisplayName("toTreeDto는 그룹 정보와 전달받은 children을 그대로 담는다")
    void toTreeDto_wrapsGroupWithGivenChildren() {
        CodeGroup group = group();
        ReflectionTestUtils.setField(group, "id", 10L);
        List<CodeTreeResponse> children = CodeTreeResponse.toDetailListDto(
                List.of(detail(1L, group, null, "A", 1)));

        CodeTreeResponse tree = CodeTreeResponse.toTreeDto(group, children);

        assertThat(tree.id()).isEqualTo(10L);
        assertThat(tree.code()).isEqualTo("GROUP");
        assertThat(tree.name()).isEqualTo("그룹");
        assertThat(tree.children()).isEqualTo(children);
    }
}
