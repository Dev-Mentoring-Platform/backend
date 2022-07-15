package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.respository.FileRepository;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.project.mentoridge.config.init.TestDataBuilder.getFileRequest;
import static org.mockito.Mockito.verify;

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
}