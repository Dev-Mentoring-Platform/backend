package com.project.mentoridge.modules.upload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.upload.controller.request.UploadImageRequest;
import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.service.UploadService;
import com.project.mentoridge.modules.upload.vo.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UploadControllerTest {

    private final String BASE_URL = "/api/uploads";

    @InjectMocks
    UploadController uploadController;
    @Mock
    UploadService uploadService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void uploadImage() throws Exception {

        // given
        File file = File.builder()
                .uuid("uuid")
                .type(FileType.LECTURE_IMAGE)
                .name("file")
                .contentType("image/jpg")
                .size(2424L)
                .build();
        UploadResponse response = new UploadResponse(new FileResponse(file), "url");

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
                FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream()));
        doReturn(response)
                .when(uploadService).uploadImage("image", multipartFile);
        // when
        // then
        UploadImageRequest request = UploadImageRequest.builder()
                .file(multipartFile)
                .build();
        mockMvc.perform(multipart(BASE_URL + "/images")
                .file((MockMultipartFile) request.getFile())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

}
