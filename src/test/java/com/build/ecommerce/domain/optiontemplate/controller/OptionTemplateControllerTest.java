package com.build.ecommerce.domain.optiontemplate.controller;

import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateRequest;
import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateValueRequest;
import com.build.ecommerce.helper.UnitTestHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OptionTemplateControllerTest extends UnitTestHelper {

    private OptionTemplateRequest sizeTemplateRequest() {
        return new OptionTemplateRequest(
                "사이즈",
                List.of(
                        new OptionTemplateValueRequest("S", 0),
                        new OptionTemplateValueRequest("M", 1),
                        new OptionTemplateValueRequest("L", 2)
                )
        );
    }

    private long registerOptionTemplateReturningId(OptionTemplateRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/option-template")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("optionTemplateId").asLong();
    }

    @Test
    @DisplayName("옵션 템플릿 등록 성공")
    void registerOptionTemplateTest() throws Exception {
        mockMvc.perform(post("/v1/option-template")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(sizeTemplateRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.optionTemplateId").isNumber());
    }

    @Test
    @DisplayName("옵션 템플릿 등록 실패 - 관리자 권한 없음")
    void registerOptionTemplateForbiddenTest() throws Exception {
        mockMvc.perform(post("/v1/option-template")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                        .content(objectMapper.writeValueAsString(sizeTemplateRequest())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("옵션 템플릿 등록 실패 - 옵션 값 누락")
    void registerOptionTemplateEmptyValuesTest() throws Exception {
        OptionTemplateRequest request = new OptionTemplateRequest("사이즈", List.of());

        mockMvc.perform(post("/v1/option-template")
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("옵션 템플릿 목록 조회 성공")
    void getOptionTemplateListTest() throws Exception {
        registerOptionTemplateReturningId(sizeTemplateRequest());

        mockMvc.perform(get("/v1/option-template")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("옵션 템플릿 상세 조회 성공")
    void getOptionTemplateDetailTest() throws Exception {
        long optionTemplateId = registerOptionTemplateReturningId(sizeTemplateRequest());

        mockMvc.perform(get("/v1/option-template/{optionTemplateId}", optionTemplateId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.optionTemplateId").value(optionTemplateId))
                .andExpect(jsonPath("$.data.name").value("사이즈"))
                .andExpect(jsonPath("$.data.optionTemplateValues.length()").value(3));
    }

    @Test
    @DisplayName("옵션 템플릿 상세 조회 실패 - 존재하지 않는 템플릿")
    void getOptionTemplateDetailFailTest() throws Exception {
        mockMvc.perform(get("/v1/option-template/{optionTemplateId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("옵션 템플릿 수정 성공")
    void updateOptionTemplateTest() throws Exception {
        long optionTemplateId = registerOptionTemplateReturningId(sizeTemplateRequest());

        OptionTemplateRequest updateRequest = new OptionTemplateRequest(
                "색상",
                List.of(
                        new OptionTemplateValueRequest("블랙", 0),
                        new OptionTemplateValueRequest("화이트", 1)
                )
        );

        mockMvc.perform(patch("/v1/option-template/{optionTemplateId}", optionTemplateId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("색상"))
                .andExpect(jsonPath("$.data.optionTemplateValues.length()").value(2));
    }

    @Test
    @DisplayName("옵션 템플릿 삭제 성공")
    void deleteOptionTemplateTest() throws Exception {
        long optionTemplateId = registerOptionTemplateReturningId(sizeTemplateRequest());

        mockMvc.perform(delete("/v1/option-template/{optionTemplateId}", optionTemplateId)
                        .headers(getHeaderSetting())
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/option-template/{optionTemplateId}", optionTemplateId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
