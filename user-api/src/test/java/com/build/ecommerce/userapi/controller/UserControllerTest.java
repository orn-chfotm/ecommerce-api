package com.build.ecommerce.userapi.controller;

import com.build.ecommerce.domain.user.dto.request.UserRequest;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends UnitTestHelper {

    @Test
    @DisplayName("회원가입")
    void signUpTest() throws Exception {
        UserRequest userRequest = new UserRequest(
                "test2@email.com",
                "testPassword",
                "testUser",
                "M",
                "2024-01-08"
        );

        mockMvc.perform(post("/v1/user")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(userRequest))
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("회원 정보 조회")
    void getUserInfo() throws Exception {
        mockMvc.perform(get("/v1/user")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken())
                ).andDo(print())
                .andExpect(status().isOk());
    }
}