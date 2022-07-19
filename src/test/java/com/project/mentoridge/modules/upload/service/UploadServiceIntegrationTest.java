package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

@ServiceTest
public class UploadServiceIntegrationTest {

    @Autowired
    UploadService uploadService;

    @Test
    void uploadImage() throws Exception {

        // given
        String name = "test.png";
        MockMultipartFile mockFile = new MockMultipartFile("file",
                name,
                MediaType.IMAGE_PNG_VALUE,
                FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream()));

        // when
        UploadResponse uploadResponse = uploadService.uploadImage("image", mockFile);
        // then
        assertAll(
                () -> assertThat(uploadResponse).isNotNull(),
                () -> assertThat(uploadResponse).extracting("url").isNotNull(),
                () -> assertThat(uploadResponse).extracting("file.id").isNotNull(),
                () -> assertThat(uploadResponse).extracting("file.uuid").isNotNull(),
                () -> assertThat(uploadResponse).extracting("file.name").isEqualTo(name),
                () -> assertThat(uploadResponse).extracting("file.contentType").isEqualTo(mockFile.getContentType()),
                () -> assertThat(uploadResponse).extracting("file.type").isEqualTo(FileType.LECTURE_IMAGE.getType()),
                () -> assertThat(uploadResponse).extracting("file.size").isEqualTo(mockFile.getSize())
        );
    }

}