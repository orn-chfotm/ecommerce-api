package com.build.ecommerce.adminapi.code.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailUpdateRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupSortOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CodeControllerTest extends UnitTestHelper {

    private CodeGroupRegisterRequest codeGroupRequest(String code) {
        return new CodeGroupRegisterRequest(code, code + "-이름", 1, true);
    }

    private CodeGroupRegisterRequest codeGroupRequest(String code, int sortOrder) {
        return new CodeGroupRegisterRequest(code, code + "-이름", sortOrder, true);
    }

    private long registerCodeGroupReturningId(CodeGroupRegisterRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/code-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("id").asLong();
    }

    private CodeDetailRegisterRequest codeDetailRequest(Long parentId, String code) {
        return new CodeDetailRegisterRequest(parentId, code, code + "-이름", true);
    }

    private long registerCodeDetailReturningId(long codeGroupId, CodeDetailRegisterRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/code-groups/{codeGroupId}/code-details", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("id").asLong();
    }

    @Test
    @DisplayName("코드 그룹 등록 성공")
    void registerCodeGroupTest() throws Exception {
        mockMvc.perform(post("/v1/code-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(codeGroupRequest("REG_TEST"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("REG_TEST"))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("코드 트리 목록 조회 - 자식 없이 id/code/name/children만 내려온다")
    void getCodeTreeTest() throws Exception {
        registerCodeGroupReturningId(codeGroupRequest("TREE_TEST"));

        MvcResult result = mockMvc.perform(get("/v1/code-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode treeGroupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "TREE_TEST".equals(node.get("code").asText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TREE_TEST 그룹이 목록에 없습니다."));

        assertThat(treeGroupNode.has("sortOrder")).isFalse();
        assertThat(treeGroupNode.has("active")).isFalse();
        assertThat(treeGroupNode.get("children").isEmpty()).isTrue();
    }

    @Test
    @DisplayName("코드 그룹 상세 조회 성공 - sortOrder/active 포함")
    void getCodeGroupDetailTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DETAIL_TEST"));

        mockMvc.perform(get("/v1/code-groups/{codeGroupId}", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(codeGroupId))
                .andExpect(jsonPath("$.data.code").value("DETAIL_TEST"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("코드 그룹 상세 조회 실패 - 존재하지 않는 그룹")
    void getCodeGroupDetailNotFoundTest() throws Exception {
        mockMvc.perform(get("/v1/code-groups/{codeGroupId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 코드 조회 실패 - 존재하지 않는 상세 코드")
    void getCodeDetailDetailNotFoundTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DETAIL_NF_TEST"));

        mockMvc.perform(get("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("최상위 상세 코드 등록 성공 - parentId 없이 등록된다")
    void registerCodeDetailRootTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DETAIL_REG_ROOT"));

        mockMvc.perform(post("/v1/code-groups/{codeGroupId}/code-details", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(codeDetailRequest(null, "ROOT"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("ROOT"))
                .andExpect(jsonPath("$.data.depth").value(0));
    }

    @Test
    @DisplayName("상세 코드 등록 실패 - 존재하지 않는 그룹")
    void registerCodeDetailGroupNotFoundTest() throws Exception {
        mockMvc.perform(post("/v1/code-groups/{codeGroupId}/code-details", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(codeDetailRequest(null, "ROOT"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 코드 등록 실패 - 존재하지 않는 부모 상세 코드")
    void registerCodeDetailParentNotFoundTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DETAIL_REG_PNF"));

        mockMvc.perform(post("/v1/code-groups/{codeGroupId}/code-details", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(codeDetailRequest(999999L, "CHILD"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 코드 상세 조회 성공 - sortOrder/active/depth 포함")
    void getCodeDetailDetailTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DETAIL_VIEW_TEST"));
        long codeDetailId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "ROOT2"));

        mockMvc.perform(get("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(codeDetailId))
                .andExpect(jsonPath("$.data.code").value("ROOT2"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.depth").value(0));
    }

    @Test
    @DisplayName("코드 트리 목록 조회 - 자식(손자 포함)까지 중첩 트리로 내려온다")
    void getCodeTreeWithNestedDetailsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("TREE_NESTED_TEST"));
        long rootId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "P"));
        long childId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(rootId, "P-1"));
        registerCodeDetailReturningId(codeGroupId, codeDetailRequest(childId, "P-1-1"));

        MvcResult result = mockMvc.perform(get("/v1/code-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode groupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "TREE_NESTED_TEST".equals(node.get("code").asText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TREE_NESTED_TEST 그룹이 목록에 없습니다."));

        JsonNode rootNode = groupNode.get("children").get(0);
        assertThat(rootNode.get("code").asText()).isEqualTo("P");
        JsonNode childNode = rootNode.get("children").get(0);
        assertThat(childNode.get("code").asText()).isEqualTo("P-1");
        JsonNode grandchildNode = childNode.get("children").get(0);
        assertThat(grandchildNode.get("code").asText()).isEqualTo("P-1-1");
        assertThat(grandchildNode.get("children").isEmpty()).isTrue();
    }

    private int getSortOrder(long codeGroupId, long codeDetailId) throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("sortOrder").asInt();
    }

    @Test
    @DisplayName("상세 코드 등록 시 정렬 순서가 형제 중 맨 끝(최댓값+1)으로 자동 부여된다")
    void registerCodeDetailAutoAppendSortOrderTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("SORT_APPEND_TEST"));

        long idA = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "APPEND_A"));
        long idB = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "APPEND_B"));
        long idC = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "APPEND_C"));

        assertThat(getSortOrder(codeGroupId, idA)).isEqualTo(1);
        assertThat(getSortOrder(codeGroupId, idB)).isEqualTo(2);
        assertThat(getSortOrder(codeGroupId, idC)).isEqualTo(3);
    }

    @Test
    @DisplayName("뒤 항목을 앞으로 이동하면 사이 구간이 전부 +1 밀린다")
    void moveCodeDetailUpShiftsBetweenRangeTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_UP_TEST"));
        long idA = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MU_A"));
        long idB = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MU_B"));
        long idC = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MU_C"));
        long idD = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MU_D"));

        // D(4) -> target 2 : [2,4) 구간(B,C) +1, D=2
        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, idD)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(2, null))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(2));

        assertThat(getSortOrder(codeGroupId, idA)).isEqualTo(1);
        assertThat(getSortOrder(codeGroupId, idD)).isEqualTo(2);
        assertThat(getSortOrder(codeGroupId, idB)).isEqualTo(3);
        assertThat(getSortOrder(codeGroupId, idC)).isEqualTo(4);
    }

    @Test
    @DisplayName("앞 항목을 뒤로 이동하면 사이 구간이 전부 -1 밀린다")
    void moveCodeDetailDownShiftsBetweenRangeTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_DOWN_TEST"));
        long idA = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MD_A"));
        long idB = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MD_B"));
        long idC = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MD_C"));
        long idD = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MD_D"));

        // A(1) -> target 3 : (1,3] 구간(B,C) -1, A=3
        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(3, null))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(3));

        assertThat(getSortOrder(codeGroupId, idB)).isEqualTo(1);
        assertThat(getSortOrder(codeGroupId, idC)).isEqualTo(2);
        assertThat(getSortOrder(codeGroupId, idA)).isEqualTo(3);
        assertThat(getSortOrder(codeGroupId, idD)).isEqualTo(4);
    }

    @Test
    @DisplayName("범위 밖 순서로 이동 요청 시 400")
    void moveCodeDetailOutOfRangeTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_RANGE_TEST"));
        long idA = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MR_A"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(5, null))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상세 코드 수정 성공 - name/sortOrder/active가 반영된다")
    void updateCodeDetailTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("UPDATE_TEST"));
        long codeDetailId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "UPDATE_A"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailUpdateRequest("수정된 이름", 1, false))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 이름"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("상세 코드 수정 실패 - 다른 그룹 소속의 상세 코드 id로 요청하면 404")
    void updateCodeDetailGroupMismatchTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("UPDATE_MISMATCH_A"));
        long otherCodeGroupId = registerCodeGroupReturningId(codeGroupRequest("UPDATE_MISMATCH_B"));
        long codeDetailId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MISMATCH_A"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", otherCodeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailUpdateRequest("수정된 이름", 1, false))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 코드 이동 실패 - 다른 그룹 소속의 상세 코드 id로 요청하면 404")
    void moveCodeDetailGroupMismatchTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_MISMATCH_A"));
        long otherCodeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_MISMATCH_B"));
        long codeDetailId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MOVE_MISMATCH_A"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", otherCodeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(1, null))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private int getGroupSortOrder(long codeGroupId) throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/code-groups/{codeGroupId}", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("sortOrder").asInt();
    }

    @Test
    @DisplayName("코드 그룹 순서 이동 성공 - 사이 구간이 밀린다")
    void moveCodeGroupSortOrderTest() throws Exception {
        // 다른 테스트가 남긴 데이터와 겹치지 않도록 충분히 큰 정렬 순서를 사용한다.
        long idA = registerCodeGroupReturningId(codeGroupRequest("GROUP_MOVE_A", 9001));
        long idB = registerCodeGroupReturningId(codeGroupRequest("GROUP_MOVE_B", 9002));
        long idC = registerCodeGroupReturningId(codeGroupRequest("GROUP_MOVE_C", 9003));
        long idD = registerCodeGroupReturningId(codeGroupRequest("GROUP_MOVE_D", 9004));

        // D(9004) -> target 9002 : [9002,9004) 구간(B,C) +1, D=9002
        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/sort-order", idD)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeGroupSortOrderMoveRequest(9002))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(9002));

        assertThat(getGroupSortOrder(idA)).isEqualTo(9001);
        assertThat(getGroupSortOrder(idD)).isEqualTo(9002);
        assertThat(getGroupSortOrder(idB)).isEqualTo(9003);
        assertThat(getGroupSortOrder(idC)).isEqualTo(9004);
    }

    @Test
    @DisplayName("코드 그룹 순서 이동 실패 - 범위 밖 순서로 요청하면 400")
    void moveCodeGroupSortOrderOutOfRangeTest() throws Exception {
        long idA = registerCodeGroupReturningId(codeGroupRequest("GROUP_RANGE_A"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/sort-order", idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeGroupSortOrderMoveRequest(999999999))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상세 코드를 다른 부모로 이동하면 원래 부모/새 부모 양쪽의 형제 순서가 shift되고 트리 소속도 바뀐다")
    void moveCodeDetailToNewParentTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_PARENT_TEST"));
        long p1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MP_P1"));
        long p2 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MP_P2"));
        long c1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p1, "MP_C1"));
        long c2 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p1, "MP_C2"));
        long d1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p2, "MP_D1"));

        // C1을 P2의 자식으로, 맨 앞(순서 1)으로 이동
        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, c1)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(1, p2))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.depth").value(1));

        // 원래 부모(P1)에 남은 C2는 순서가 2->1로 당겨진다
        assertThat(getSortOrder(codeGroupId, c2)).isEqualTo(1);
        // 새 부모(P2)의 기존 자식 D1은 1->2로 밀린다
        assertThat(getSortOrder(codeGroupId, d1)).isEqualTo(2);

        MvcResult result = mockMvc.perform(get("/v1/code-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode groupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "MOVE_PARENT_TEST".equals(node.get("code").asText()))
                .findFirst()
                .orElseThrow();
        JsonNode p1Node = StreamSupport.stream(groupNode.get("children").spliterator(), false)
                .filter(node -> "MP_P1".equals(node.get("code").asText()))
                .findFirst().orElseThrow();
        JsonNode p2Node = StreamSupport.stream(groupNode.get("children").spliterator(), false)
                .filter(node -> "MP_P2".equals(node.get("code").asText()))
                .findFirst().orElseThrow();

        assertThat(StreamSupport.stream(p1Node.get("children").spliterator(), false)
                .map(n -> n.get("code").asText()).toList())
                .containsExactly("MP_C2");
        assertThat(StreamSupport.stream(p2Node.get("children").spliterator(), false)
                .map(n -> n.get("code").asText()).toList())
                .contains("MP_C1", "MP_D1");
    }

    @Test
    @DisplayName("자식이 있는 상세 코드는 다른 부모로 이동할 수 없다 - 409")
    void moveCodeDetailWithChildrenToNewParentFailsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_HAS_CHILD_TEST"));
        long p1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MHC_P1"));
        long p2 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MHC_P2"));
        long c1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p1, "MHC_C1"));
        registerCodeDetailReturningId(codeGroupId, codeDetailRequest(c1, "MHC_GC1"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, c1)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(1, p2))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("최상위(depth 0)가 아닌 상세 코드로는 이동할 수 없다 - 400")
    void moveCodeDetailToNonRootParentFailsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("MOVE_NON_ROOT_TEST"));
        long p1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MNR_P1"));
        long c1 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p1, "MNR_C1"));
        long p3 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "MNR_P3"));
        long c3 = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(p3, "MNR_C3"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", codeGroupId, c3)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(1, c1))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("다른 코드 그룹에 속한 상세 코드로는 이동할 수 없다 - 404")
    void moveCodeDetailAcrossGroupFailsTest() throws Exception {
        long groupAId = registerCodeGroupReturningId(codeGroupRequest("MOVE_GROUP_A"));
        long groupBId = registerCodeGroupReturningId(codeGroupRequest("MOVE_GROUP_B"));
        long targetParentInGroupA = registerCodeDetailReturningId(groupAId, codeDetailRequest(null, "MXG_A_ROOT"));
        long currentInGroupB = registerCodeDetailReturningId(groupBId, codeDetailRequest(null, "MXG_B_ROOT"));

        mockMvc.perform(patch("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}/sort-order", groupBId, currentInGroupB)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new CodeDetailSortOrderMoveRequest(1, targetParentInGroupA))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("하위 상세 코드가 없는 코드 그룹은 삭제된다")
    void deleteCodeGroupTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DEL_GROUP_OK"));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/code-groups/{codeGroupId}", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("하위 상세 코드가 있는 코드 그룹은 삭제할 수 없다 - 409")
    void deleteCodeGroupWithChildrenFailsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DEL_GROUP_FAIL"));
        registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DGF_ROOT"));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}", codeGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("자식 상세 코드가 없는 상세 코드는 삭제된다")
    void deleteCodeDetailTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DEL_DETAIL_OK"));
        long codeDetailId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DDO_ROOT"));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, codeDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("자식 상세 코드가 있는 상세 코드는 삭제할 수 없다 - 409")
    void deleteCodeDetailWithChildrenFailsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DEL_DETAIL_FAIL"));
        long parentId = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DDF_PARENT"));
        registerCodeDetailReturningId(codeGroupId, codeDetailRequest(parentId, "DDF_CHILD"));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, parentId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("코드 그룹 삭제 시 뒤에 있는 형제들의 정렬 순서가 당겨진다")
    void deleteCodeGroupShiftsRemainingSiblingsTest() throws Exception {
        // 다른 테스트가 남긴 데이터와 겹치지 않도록 충분히 큰 정렬 순서를 사용한다.
        long idA = registerCodeGroupReturningId(codeGroupRequest("DEL_SHIFT_A", 9301));
        long idB = registerCodeGroupReturningId(codeGroupRequest("DEL_SHIFT_B", 9302));
        long idC = registerCodeGroupReturningId(codeGroupRequest("DEL_SHIFT_C", 9303));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}", idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(getGroupSortOrder(idB)).isEqualTo(9301);
        assertThat(getGroupSortOrder(idC)).isEqualTo(9302);
    }

    @Test
    @DisplayName("상세 코드 삭제 시 같은 부모의 뒤에 있는 형제들 정렬 순서가 당겨진다")
    void deleteCodeDetailShiftsRemainingSiblingsTest() throws Exception {
        long codeGroupId = registerCodeGroupReturningId(codeGroupRequest("DEL_DETAIL_SHIFT"));
        long idA = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DDS_A"));
        long idB = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DDS_B"));
        long idC = registerCodeDetailReturningId(codeGroupId, codeDetailRequest(null, "DDS_C"));

        mockMvc.perform(delete("/v1/code-groups/{codeGroupId}/code-details/{codeDetailId}", codeGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(getSortOrder(codeGroupId, idB)).isEqualTo(1);
        assertThat(getSortOrder(codeGroupId, idC)).isEqualTo(2);
    }
}
