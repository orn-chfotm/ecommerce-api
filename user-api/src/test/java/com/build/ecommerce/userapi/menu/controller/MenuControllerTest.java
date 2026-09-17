package com.build.ecommerce.userapi.menu.controller;

import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailRegisterRequest;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupRegisterRequest;
import com.build.ecommerce.domain.menu.service.MenuService;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTest extends UnitTestHelper {

    @Autowired
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 트리 목록 조회 - 사용자 토큰으로 조회 가능하다")
    void getMenuTreeTest() throws Exception {
        menuService.registerMenuGroup(new MenuGroupRegisterRequest("USER_TREE_TEST", "url", 1, true));

        mockMvc.perform(get("/v1/menu-groups")
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("메뉴 그룹 상세 조회 성공 - 사용자 토큰으로 조회 가능하다")
    void getMenuGroupDetailTest() throws Exception {
        long menuGroupId = menuService.registerMenuGroup(
                new MenuGroupRegisterRequest("USER_DETAIL_TEST", "url", 1, true)).id();

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(menuGroupId))
                .andExpect(jsonPath("$.data.name").value("USER_DETAIL_TEST"));
    }

    @Test
    @DisplayName("메뉴 그룹 상세 조회 실패 - 존재하지 않는 그룹")
    void getMenuGroupDetailNotFoundTest() throws Exception {
        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}", 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 상세 조회 성공 - 사용자 토큰으로 조회 가능하다")
    void getMenuDetailDetailTest() throws Exception {
        long menuGroupId = menuService.registerMenuGroup(
                new MenuGroupRegisterRequest("USER_DETAIL_VIEW_TEST", "url", 1, true)).id();
        long menuDetailId = menuService.registerMenuDetail(
                menuGroupId, new MenuDetailRegisterRequest(null, "USER_ROOT", "url", true)).id();

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, menuDetailId)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(menuDetailId))
                .andExpect(jsonPath("$.data.name").value("USER_ROOT"));
    }

    @Test
    @DisplayName("메뉴 상세 조회 실패 - 존재하지 않는 메뉴 상세")
    void getMenuDetailDetailNotFoundTest() throws Exception {
        long menuGroupId = menuService.registerMenuGroup(
                new MenuGroupRegisterRequest("USER_DETAIL_NF_TEST", "url", 1, true)).id();

        mockMvc.perform(get("/v1/menu-groups/{menuGroupId}/menu-details/{menuDetailId}", menuGroupId, 999999L)
                        .headers(getHeaderSetting())
                        .headers(getAccessToken()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
