package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class UploadServiceIntegrationTest {

    @Autowired
    private UploadService uploadService;

    // TODO - 업로드 재구현
    // @Test
    void 이미지업로드_테스트() throws Exception {

        // given
        String name = "test.png";
        MockMultipartFile mockFile = new MockMultipartFile("file",
                name,
                MediaType.IMAGE_PNG_VALUE,
                FileCopyUtils.copyToByteArray(new ClassPathResource("image/test.png").getInputStream()));

        // when
        UploadResponse uploadResponse = uploadService.uploadImage("image", mockFile);
        // then
        assertThat(uploadResponse).isNotNull();
        assertThat(uploadResponse).extracting("url").isNotNull();
        assertThat(uploadResponse).extracting("file.uuid").isNotNull();
        assertThat(uploadResponse).extracting("file.name").isEqualTo(name);
        assertThat(uploadResponse).extracting("file.size").isEqualTo(mockFile.getSize());
        assertThat(uploadResponse).extracting("file.contentType").isEqualTo(mockFile.getContentType());
        assertThat(uploadResponse).extracting("file.type").isEqualTo(FileType.LECTURE_IMAGE.getType());
    }

}