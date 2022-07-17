package com.project.mentoridge.modules.upload.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.upload.controller.request.UploadImageRequest;
import com.project.mentoridge.modules.upload.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UploadControllerTest extends AbstractControllerTest {

    private final String BASE_URL = "/api/uploads";

    @InjectMocks
    UploadController uploadController;
    @Mock
    UploadService uploadService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void upload_image() throws Exception {

        // given
/*
        File file = File.builder()
                .uuid("uuid")
                .type(FileType.LECTURE_IMAGE)
                .name("file")
                .contentType("image/jpg")
                .size(2424L)
                .build();
        UploadResponse response = new UploadResponse(new FileResponse(file), "url");*/
        // when
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
                FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream()));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(multipartFile)
                .build();
        mockMvc.perform(multipart(BASE_URL + "/images")
                        .file((MockMultipartFile) request.getFile())
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(uploadService).uploadImage(any(), any(MultipartFile.class));
    }

}
