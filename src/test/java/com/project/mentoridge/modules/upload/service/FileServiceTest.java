package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.respository.FileRepository;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import com.project.mentoridge.modules.upload.vo.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.project.mentoridge.config.init.TestDataBuilder.getFileRequest;
import static org.mockito.Mockito.*;

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
        File file = mock(File.class);
        when(fileRepository.save(fileRequest.toEntity())).thenReturn(file);

        // when
        File created = fileService.createFile(fileRequest);
        // then
        verify(fileRepository).save(file);
    }
}