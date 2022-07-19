package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.respository.FileRepository;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import com.project.mentoridge.modules.upload.vo.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.project.mentoridge.config.init.TestDataBuilder.getFileRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    FileServiceImpl fileService;
    @Mock
    FileRepository fileRepository;

    @Test
    void create_file() {

        // given
        FileRequest fileRequest = getFileRequest();
        // when
        fileService.createFile(fileRequest);
        // then
        verify(fileRepository).save(fileRequest.toEntity());
    }

    @Test
    void get_file() {

        // given
        File file = File.builder()
                .uuid("uuid")
                .type(FileType.LECTURE_IMAGE)
                .name("name")
                .contentType("contentType")
                .size(50L)
                .build();
        when(fileRepository.findByUuid("uuid")).thenReturn(file);
        // when
        FileResponse response = fileService.getFile("uuid");
        // then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(file.getId()),
                () -> assertThat(response.getUuid()).isEqualTo(file.getUuid()),
                () -> assertThat(response.getName()).isEqualTo(file.getName()),
                () -> assertThat(response.getContentType()).isEqualTo(file.getContentType()),
                () -> assertThat(response.getType()).isEqualTo(file.getType().getType()),
                () -> assertThat(response.getSize()).isEqualTo(file.getSize())
        );
    }
}