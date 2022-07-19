package com.project.mentoridge.modules.upload.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.upload.controller.request.UploadImageRequest;
import com.project.mentoridge.modules.upload.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UploadController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
public class UploadControllerTest extends AbstractControllerTest {

    private final String BASE_URL = "/api/uploads";

    @MockBean
    UploadService uploadService;


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
