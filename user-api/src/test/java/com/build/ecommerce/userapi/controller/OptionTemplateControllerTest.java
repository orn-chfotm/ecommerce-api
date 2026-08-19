package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateRequest;
import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateValueRequest;
import com.build.ecommerce.domain.optiontemplate.service.OptionTemplateService;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OptionTemplateControllerTest extends UnitTestHelper {

    @Autowired
    private OptionTemplateService optionTemplateService;

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

    @Test
    @DisplayName("옵션 템플릿 목록 조회 성공")
    void getOptionTemplateListTest() throws Exception {
        optionTemplateService.insertOptionTemplate(sizeTemplateRequest());

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
        long optionTemplateId = optionTemplateService.insertOptionTemplate(sizeTemplateRequest()).optionTemplateId();

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
}
