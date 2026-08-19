package com.build.ecommerce.adminapi.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends UnitTestHelper {

    @Test
    @DisplayName("제품 등록 - 파일 없음")
    void productInsertTest() throws Exception {
        mockMvc.perform(multipart("/v1/product")
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 - 파일 포함")
    void productInsertWithFilesTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "image1.jpg", "image/jpeg", "test-image-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "image2.jpg", "image/jpeg", "test-image-2".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file1)
                        .file(file2)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("제품 등록 실패 - 파일 개수 초과")
    void productInsertFileExceedTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "image1.jpg", "image/jpeg", "test-image-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "image2.jpg", "image/jpeg", "test-image-2".getBytes());
        MockMultipartFile file3 = new MockMultipartFile(
                "files", "image3.jpg", "image/jpeg", "test-image-3".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("제품 등록 실패 - 허용되지 않는 파일 형식")
    void productInsertInvalidExtensionTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "script.exe", "application/octet-stream", "malicious".getBytes());

        mockMvc.perform(multipart("/v1/product")
                        .file(file)
                        .param("category", "FASHION")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("제품 등록 실패 - 필수값 누락")
    void productInsertFailTest() throws Exception {
        mockMvc.perform(multipart("/v1/product")
                        .param("category", "")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        mockMvc.perform(multipart("/v1/product")
                        .param("category", "fasion")
                        .param("name", "장갑")
                        .param("description", "따뜻한 장갑")
                        .param("price", "10000")
                        .param("stockQuantity", "100")
                        .param("minOrderQuantity", "1")
                        .param("active", "true")
                        .headers(getAdminAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
