package com.build.ecommerce.adminapi.menu.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailUpdateRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupSortOrderMoveRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupRegisterRequest;
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

class MenuControllerTest extends UnitTestHelper {

    private MenuGroupRegisterRequest menuGroupRequest(String name) {
        return new MenuGroupRegisterRequest(name, name + "-url", 1, true);
    }

    private MenuGroupRegisterRequest menuGroupRequest(String name, int sortOrder) {
        return new MenuGroupRegisterRequest(name, name + "-url", sortOrder, true);
    }

    private long registerMenuGroupReturningId(MenuGroupRegisterRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("id").asLong();
    }

    private MenuDetailRegisterRequest menuDetailRequest(Long parentId, String name) {
        return new MenuDetailRegisterRequest(parentId, name, name + "-url", true);
    }

    private long registerMenuDetailReturningId(long menuGroupId, MenuDetailRegisterRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/menu-groups/{menuGroupId}/menu-details", menuGroupId)
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
    @DisplayName("메뉴 그룹 등록 성공")
    void registerMenuGroupTest() throws Exception {
        mockMvc.perform(post("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(menuGroupRequest("REG_TEST"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("REG_TEST"))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("메뉴 트리 목록 조회 - 자식 없이 id/name/children만 내려온다")
    void getMenuTreeTest() throws Exception {
        registerMenuGroupReturningId(menuGroupRequest("TREE_TEST"));

        MvcResult result = mockMvc.perform(get("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode treeGroupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "TREE_TEST".equals(node.get("name").asText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TREE_TEST 그룹이 목록에 없습니다."));

        assertThat(treeGroupNode.has("sortOrder")).isFalse();
        assertThat(treeGroupNode.has("active")).isFalse();
        assertThat(treeGroupNode.get("children").isEmpty()).isTrue();
    }

    @Test
    @DisplayName("메뉴 그룹 상세 조회 성공 - sortOrder/active 포함")
    void getMenuGroupDetailTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DETAIL_TEST"));

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(menuGroupId))
                .andExpect(jsonPath("$.data.name").value("DETAIL_TEST"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("메뉴 그룹 상세 조회 실패 - 존재하지 않는 그룹")
    void getMenuGroupDetailNotFoundTest() throws Exception {
        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 상세 조회 실패 - 존재하지 않는 메뉴 상세")
    void getMenuDetailDetailNotFoundTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DETAIL_NF_TEST"));

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("최상위 메뉴 상세 등록 성공 - parentId 없이 등록된다")
    void registerMenuDetailRootTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DETAIL_REG_ROOT"));

        mockMvc.perform(post("/v1/menu-groups/{menuGroupId}/menu-details", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(menuDetailRequest(null, "ROOT"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("ROOT"))
                .andExpect(jsonPath("$.data.depth").value(0));
    }

    @Test
    @DisplayName("메뉴 상세 등록 실패 - 존재하지 않는 그룹")
    void registerMenuDetailGroupNotFoundTest() throws Exception {
        mockMvc.perform(post("/v1/menu-groups/{menuGroupId}/menu-details", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(menuDetailRequest(null, "ROOT"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 상세 등록 실패 - 존재하지 않는 부모 메뉴 상세")
    void registerMenuDetailParentNotFoundTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DETAIL_REG_PNF"));

        mockMvc.perform(post("/v1/menu-groups/{menuGroupId}/menu-details", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(menuDetailRequest(999999L, "CHILD"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 상세 상세 조회 성공 - sortOrder/active/depth 포함")
    void getMenuDetailDetailTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DETAIL_VIEW_TEST"));
        long menuDetailId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "ROOT2"));

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(menuDetailId))
                .andExpect(jsonPath("$.data.name").value("ROOT2"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.depth").value(0));
    }

    @Test
    @DisplayName("메뉴 트리 목록 조회 - 자식(손자 포함)까지 중첩 트리로 내려온다")
    void getMenuTreeWithNestedDetailsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("TREE_NESTED_TEST"));
        long rootId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "P"));
        long childId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(rootId, "P-1"));
        registerMenuDetailReturningId(menuGroupId, menuDetailRequest(childId, "P-1-1"));

        MvcResult result = mockMvc.perform(get("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode groupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "TREE_NESTED_TEST".equals(node.get("name").asText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TREE_NESTED_TEST 그룹이 목록에 없습니다."));

        JsonNode rootNode = groupNode.get("children").get(0);
        assertThat(rootNode.get("name").asText()).isEqualTo("P");
        JsonNode childNode = rootNode.get("children").get(0);
        assertThat(childNode.get("name").asText()).isEqualTo("P-1");
        JsonNode grandchildNode = childNode.get("children").get(0);
        assertThat(grandchildNode.get("name").asText()).isEqualTo("P-1-1");
        assertThat(grandchildNode.get("children").isEmpty()).isTrue();
    }

    private int getSortOrder(long menuGroupId, long menuDetailId) throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("sortOrder").asInt();
    }

    @Test
    @DisplayName("메뉴 상세 등록 시 정렬 순서가 형제 중 맨 끝(최댓값+1)으로 자동 부여된다")
    void registerMenuDetailAutoAppendSortOrderTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("SORT_APPEND_TEST"));

        long idA = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "APPEND_A"));
        long idB = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "APPEND_B"));
        long idC = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "APPEND_C"));

        assertThat(getSortOrder(menuGroupId, idA)).isEqualTo(1);
        assertThat(getSortOrder(menuGroupId, idB)).isEqualTo(2);
        assertThat(getSortOrder(menuGroupId, idC)).isEqualTo(3);
    }

    @Test
    @DisplayName("뒤 항목을 앞으로 이동하면 사이 구간이 전부 +1 밀린다")
    void moveMenuDetailUpShiftsBetweenRangeTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_UP_TEST"));
        long idA = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MU_A"));
        long idB = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MU_B"));
        long idC = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MU_C"));
        long idD = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MU_D"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, idD)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(2, null))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(2));

        assertThat(getSortOrder(menuGroupId, idA)).isEqualTo(1);
        assertThat(getSortOrder(menuGroupId, idD)).isEqualTo(2);
        assertThat(getSortOrder(menuGroupId, idB)).isEqualTo(3);
        assertThat(getSortOrder(menuGroupId, idC)).isEqualTo(4);
    }

    @Test
    @DisplayName("앞 항목을 뒤로 이동하면 사이 구간이 전부 -1 밀린다")
    void moveMenuDetailDownShiftsBetweenRangeTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_DOWN_TEST"));
        long idA = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MD_A"));
        long idB = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MD_B"));
        long idC = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MD_C"));
        long idD = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MD_D"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(3, null))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(3));

        assertThat(getSortOrder(menuGroupId, idB)).isEqualTo(1);
        assertThat(getSortOrder(menuGroupId, idC)).isEqualTo(2);
        assertThat(getSortOrder(menuGroupId, idA)).isEqualTo(3);
        assertThat(getSortOrder(menuGroupId, idD)).isEqualTo(4);
    }

    @Test
    @DisplayName("범위 밖 순서로 이동 요청 시 400")
    void moveMenuDetailOutOfRangeTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_RANGE_TEST"));
        long idA = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MR_A"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(5, null))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 상세 수정 성공 - name/sortOrder/active가 반영된다")
    void updateMenuDetailTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("UPDATE_TEST"));
        long menuDetailId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "UPDATE_A"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailUpdateRequest("수정된 이름", "updated-url", 1, false))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 이름"))
                .andExpect(jsonPath("$.data.url").value("updated-url"))
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("메뉴 상세 수정 실패 - 다른 그룹 소속의 메뉴 상세 id로 요청하면 404")
    void updateMenuDetailGroupMismatchTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("UPDATE_MISMATCH_A"));
        long otherMenuGroupId = registerMenuGroupReturningId(menuGroupRequest("UPDATE_MISMATCH_B"));
        long menuDetailId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MISMATCH_A"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", otherMenuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailUpdateRequest("수정된 이름", "updated-url", 1, false))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 상세 이동 실패 - 다른 그룹 소속의 메뉴 상세 id로 요청하면 404")
    void moveMenuDetailGroupMismatchTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_MISMATCH_A"));
        long otherMenuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_MISMATCH_B"));
        long menuDetailId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MOVE_MISMATCH_A"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", otherMenuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(1, null))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private int getGroupSortOrder(long menuGroupId) throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("sortOrder").asInt();
    }

    @Test
    @DisplayName("메뉴 그룹 순서 이동 성공 - 사이 구간이 밀린다")
    void moveMenuGroupSortOrderTest() throws Exception {
        // 다른 테스트가 남긴 데이터와 겹치지 않도록 충분히 큰 정렬 순서를 사용한다.
        long idA = registerMenuGroupReturningId(menuGroupRequest("GROUP_MOVE_A", 9001));
        long idB = registerMenuGroupReturningId(menuGroupRequest("GROUP_MOVE_B", 9002));
        long idC = registerMenuGroupReturningId(menuGroupRequest("GROUP_MOVE_C", 9003));
        long idD = registerMenuGroupReturningId(menuGroupRequest("GROUP_MOVE_D", 9004));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/sort-order", idD)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuGroupSortOrderMoveRequest(9002))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(9002));

        assertThat(getGroupSortOrder(idA)).isEqualTo(9001);
        assertThat(getGroupSortOrder(idD)).isEqualTo(9002);
        assertThat(getGroupSortOrder(idB)).isEqualTo(9003);
        assertThat(getGroupSortOrder(idC)).isEqualTo(9004);
    }

    @Test
    @DisplayName("메뉴 그룹 순서 이동 실패 - 범위 밖 순서로 요청하면 400")
    void moveMenuGroupSortOrderOutOfRangeTest() throws Exception {
        long idA = registerMenuGroupReturningId(menuGroupRequest("GROUP_RANGE_A"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/sort-order", idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuGroupSortOrderMoveRequest(999999999))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 상세를 다른 부모로 이동하면 원래 부모/새 부모 양쪽의 형제 순서가 shift되고 트리 소속도 바뀐다")
    void moveMenuDetailToNewParentTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_PARENT_TEST"));
        long p1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MP_P1"));
        long p2 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MP_P2"));
        long c1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p1, "MP_C1"));
        long c2 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p1, "MP_C2"));
        long d1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p2, "MP_D1"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, c1)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(1, p2))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sortOrder").value(1))
                .andExpect(jsonPath("$.data.depth").value(1));

        assertThat(getSortOrder(menuGroupId, c2)).isEqualTo(1);
        assertThat(getSortOrder(menuGroupId, d1)).isEqualTo(2);

        MvcResult result = mockMvc.perform(get("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        JsonNode groupNode = StreamSupport.stream(data.spliterator(), false)
                .filter(node -> "MOVE_PARENT_TEST".equals(node.get("name").asText()))
                .findFirst()
                .orElseThrow();
        JsonNode p1Node = StreamSupport.stream(groupNode.get("children").spliterator(), false)
                .filter(node -> "MP_P1".equals(node.get("name").asText()))
                .findFirst().orElseThrow();
        JsonNode p2Node = StreamSupport.stream(groupNode.get("children").spliterator(), false)
                .filter(node -> "MP_P2".equals(node.get("name").asText()))
                .findFirst().orElseThrow();

        assertThat(StreamSupport.stream(p1Node.get("children").spliterator(), false)
                .map(n -> n.get("name").asText()).toList())
                .containsExactly("MP_C2");
        assertThat(StreamSupport.stream(p2Node.get("children").spliterator(), false)
                .map(n -> n.get("name").asText()).toList())
                .contains("MP_C1", "MP_D1");
    }

    @Test
    @DisplayName("자식이 있는 메뉴 상세는 다른 부모로 이동할 수 없다 - 409")
    void moveMenuDetailWithChildrenToNewParentFailsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_HAS_CHILD_TEST"));
        long p1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MHC_P1"));
        long p2 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MHC_P2"));
        long c1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p1, "MHC_C1"));
        registerMenuDetailReturningId(menuGroupId, menuDetailRequest(c1, "MHC_GC1"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, c1)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(1, p2))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("최상위(depth 0)가 아닌 메뉴 상세로는 이동할 수 없다 - 400")
    void moveMenuDetailToNonRootParentFailsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("MOVE_NON_ROOT_TEST"));
        long p1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MNR_P1"));
        long c1 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p1, "MNR_C1"));
        long p3 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "MNR_P3"));
        long c3 = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(p3, "MNR_C3"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", menuGroupId, c3)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(1, c1))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("다른 메뉴 그룹에 속한 메뉴 상세로는 이동할 수 없다 - 404")
    void moveMenuDetailAcrossGroupFailsTest() throws Exception {
        long groupAId = registerMenuGroupReturningId(menuGroupRequest("MOVE_GROUP_A"));
        long groupBId = registerMenuGroupReturningId(menuGroupRequest("MOVE_GROUP_B"));
        long targetParentInGroupA = registerMenuDetailReturningId(groupAId, menuDetailRequest(null, "MXG_A_ROOT"));
        long currentInGroupB = registerMenuDetailReturningId(groupBId, menuDetailRequest(null, "MXG_B_ROOT"));

        mockMvc.perform(patch("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}/sort-order", groupBId, currentInGroupB)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(new MenuDetailSortOrderMoveRequest(1, targetParentInGroupA))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("하위 메뉴 상세가 없는 메뉴 그룹은 삭제된다")
    void deleteMenuGroupTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DEL_GROUP_OK"));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("하위 메뉴 상세가 있는 메뉴 그룹은 삭제할 수 없다 - 409")
    void deleteMenuGroupWithChildrenFailsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DEL_GROUP_FAIL"));
        registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DGF_ROOT"));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("자식 메뉴 상세가 없는 메뉴 상세는 삭제된다")
    void deleteMenuDetailTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DEL_DETAIL_OK"));
        long menuDetailId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DDO_ROOT"));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("자식 메뉴 상세가 있는 메뉴 상세는 삭제할 수 없다 - 409")
    void deleteMenuDetailWithChildrenFailsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DEL_DETAIL_FAIL"));
        long parentId = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DDF_PARENT"));
        registerMenuDetailReturningId(menuGroupId, menuDetailRequest(parentId, "DDF_CHILD"));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, parentId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("메뉴 그룹 삭제 시 뒤에 있는 형제들의 정렬 순서가 당겨진다")
    void deleteMenuGroupShiftsRemainingSiblingsTest() throws Exception {
        long idA = registerMenuGroupReturningId(menuGroupRequest("DEL_SHIFT_A", 9301));
        long idB = registerMenuGroupReturningId(menuGroupRequest("DEL_SHIFT_B", 9302));
        long idC = registerMenuGroupReturningId(menuGroupRequest("DEL_SHIFT_C", 9303));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}", idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(getGroupSortOrder(idB)).isEqualTo(9301);
        assertThat(getGroupSortOrder(idC)).isEqualTo(9302);
    }

    @Test
    @DisplayName("메뉴 상세 삭제 시 같은 부모의 뒤에 있는 형제들 정렬 순서가 당겨진다")
    void deleteMenuDetailShiftsRemainingSiblingsTest() throws Exception {
        long menuGroupId = registerMenuGroupReturningId(menuGroupRequest("DEL_DETAIL_SHIFT"));
        long idA = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DDS_A"));
        long idB = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DDS_B"));
        long idC = registerMenuDetailReturningId(menuGroupId, menuDetailRequest(null, "DDS_C"));

        mockMvc.perform(delete("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, idA)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(getSortOrder(menuGroupId, idB)).isEqualTo(1);
        assertThat(getSortOrder(menuGroupId, idC)).isEqualTo(2);
    }
}
