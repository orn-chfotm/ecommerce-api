package com.build.ecommerce.adminapi.code.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CodeControllerTest extends UnitTestHelper {

    private CodeGroupRegisterRequest codeGroupRequest(String code) {
        return new CodeGroupRegisterRequest(code, code + "-이름", 1, true);
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
        return new CodeDetailRegisterRequest(parentId, code, code + "-이름", 1, true);
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
}
