package com.project.mentoridge.modules.upload.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.upload.controller.request.UploadImageRequest;
import lombok.With;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
public class UploadControllerIntegrationTest {
// TODO - 테스트
    private final String BASE_URL = "/api/uploads";

    @Autowired
    MockMvc mockMvc;

    @WithAccount("yk")
    @Test
    void 업로드테스트() throws Exception {

        UploadImageRequest request = UploadImageRequest.builder()
                .file(new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE, FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream())))
                .build();
        mockMvc.perform(multipart(BASE_URL + "/images")
                .file((MockMultipartFile) request.getFile())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
