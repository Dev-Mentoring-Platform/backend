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
import org.mockito.Mockito;
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
    void createFile() {

        // given
        FileRequest fileRequest = getFileRequest();
        File file = fileRequest.toEntity();
        when(fileRepository.save(file)).thenReturn(file);

        // when
        FileResponse response = fileService.createFile(fileRequest);
        // then
        verify(fileRepository).save(file);
        System.out.println(response);
    }

}