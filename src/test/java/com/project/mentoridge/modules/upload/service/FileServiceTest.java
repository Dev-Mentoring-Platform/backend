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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    FileServiceImpl fileService;
    @Mock
    FileRepository fileRepository;

    @Test
    void createFile() throws IllegalAccessException, InstantiationException {

        // given
        FileRequest fileRequest = Mockito.mock(FileRequest.class);

        File file = Mockito.mock(File.class);
        when(file.getType()).thenReturn(FileType.LECTURE_IMAGE);
        when(fileRepository.save(any(File.class))).thenReturn(file);

        // when
        FileResponse response = fileService.createFile(fileRequest);

        // then
        verify(fileRepository).save(any(File.class));
        System.out.println(response);
    }

}