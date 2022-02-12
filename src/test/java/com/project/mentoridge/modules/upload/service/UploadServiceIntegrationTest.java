package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.UploadController;
import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
public class UploadServiceIntegrationTest {

    @Autowired
    private UploadService uploadService;

    @Test
    void 이미지업로드_테스트() throws Exception {
        String name = "test.png";
        MockMultipartFile mockFile = new MockMultipartFile("file",
                name,
                MediaType.IMAGE_PNG_VALUE,
                FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream()));

        UploadResponse uploadResponse = uploadService.uploadImage(UploadController.DIR, mockFile);

        assertThat(uploadResponse).isNotNull();
        assertThat(uploadResponse).extracting("url").isNotNull();
        assertThat(uploadResponse).extracting("file.uuid").isNotNull();
        assertThat(uploadResponse).extracting("file.name").isEqualTo(name);
        assertThat(uploadResponse).extracting("file.size").isEqualTo(mockFile.getSize());
        assertThat(uploadResponse).extracting("file.contentType").isEqualTo(mockFile.getContentType());
        assertThat(uploadResponse).extracting("file.type").isEqualTo(FileType.LECTURE_IMAGE.getType());
    }

}