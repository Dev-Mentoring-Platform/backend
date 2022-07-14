package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.respository.FileRepository;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import com.project.mentoridge.modules.upload.vo.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
public class FileServiceIntegrationTest {

    @Autowired
    FileService fileService;
    @Autowired
    FileRepository fileRepository;

    private final String uuid = UUID.randomUUID().toString();

    @Test
    void get_file() {

        // given
        File file = fileRepository.save(File.builder()
                .name("test.jpg")
                .contentType("image/jpg")
                .type(FileType.LECTURE_IMAGE)
                .size(2424L)
                .build());
        // when
        FileResponse response = fileService.getFile(file.getUuid());
        // then
        assertAll(
                () -> assertThat(response.getUuid()).isEqualTo(file.getUuid()),
                () -> assertThat(response.getType()).isEqualTo(file.getType().name()),
                () -> assertThat(response.getName()).isEqualTo(file.getName()),
                () -> assertThat(response.getContentType()).isEqualTo(file.getContentType()),
                () -> assertThat(response.getSize()).isEqualTo(file.getSize())
        );
    }

    @Test
    void create_file() {

        // given
        // when
        FileRequest fileRequest = FileRequest.builder()
                .uuid(uuid)
                .name("test.jpg")
                .contentType("image/jpg")
                .type(FileType.LECTURE_IMAGE)
                .size(2424L)
                .build();
        File created = fileService.createFile(fileRequest);
        // then
        File file = fileRepository.findByUuid(uuid);
        assertAll(
                () -> assertThat(created.getUuid()).isEqualTo(file.getUuid()),
                () -> assertThat(created.getType()).isEqualTo(file.getType()),
                () -> assertThat(created.getName()).isEqualTo(file.getName()),
                () -> assertThat(created.getContentType()).isEqualTo(file.getContentType()),
                () -> assertThat(created.getSize()).isEqualTo(file.getSize())
        );
    }
}