package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.config.AmazonS3Properties;
import com.project.mentoridge.modules.upload.amazon.service.AWSS3Client;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @InjectMocks
    UploadServiceImpl uploadService;

    @Mock
    AmazonS3Properties amazonS3Properties;
    @Mock
    AWSS3Client awsS3Client;
    @Mock
    FileService fileService;
/*
    @Test
    void uploadImage() throws IOException {

        // given
        when(amazonS3Properties.getBucket()).thenReturn("bucket");

        // when
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(new byte[]{});
        when(file.getContentType()).thenReturn("contentType");
        when(file.getSize()).thenReturn(0L);
        uploadService.uploadImage("/image", file);

        // then
        verify(awsS3Client).putObject("bucket", anyString(), any(byte[].class), "contentType");
        verify(fileService).createFile(any(FileRequest.class));
        verify(amazonS3Properties).getS3UploadUrl(anyString());
    }
*/

    @Test
    void uploadImage_throwException() throws IOException {

        // given
        when(amazonS3Properties.getBucket()).thenReturn("bucket");
//        doNothing().when(awss3Client).putObject(anyString(), anyString(), any(byte[].class), anyString());
//        when(fileService.createFile(any(FileRequest.class))).thenReturn(Mockito.mock(FileResponse.class));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(IOException.class);

        // when
        UploadResponse response = uploadService.uploadImage("/image", file);

        // then
        assertNull(response);
    }
}