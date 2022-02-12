package com.project.mentoridge.modules.upload.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

//    @InjectMocks
//    UploadServiceImpl uploadService;
//
//    @Mock
//    AmazonS3Properties amazonS3Properties;
//    @Mock
//    AWSS3Client awss3Client;
//    @Mock
//    FileService fileService;
//
//    @Test
//    void uploadImage() throws IOException {
//
//        // given
//        when(amazonS3Properties.getBucket()).thenReturn("bucket");
//        doNothing().when(awss3Client).putObject(anyString(), anyString(), any(byte[].class), anyString());
//        when(fileService.createFile(any(FileRequest.class))).thenReturn(Mockito.mock(FileResponse.class));
//
//        MultipartFile file = Mockito.mock(MultipartFile.class);
//        when(file.getBytes()).thenReturn(new byte[]{});
//        when(file.getContentType()).thenReturn("contentType");
//        when(file.getSize()).thenReturn(0L);
//
//        // when
//        UploadResponse response = uploadService.uploadImage("/image", file);
//
//        // then
//        verify(amazonS3Properties).getS3UploadUrl(anyString());
//        System.out.println(response);
//    }
//
//    @Test
//    void uploadImage_throwException() throws IOException {
//
//        // given
//        when(amazonS3Properties.getBucket()).thenReturn("bucket");
////        doNothing().when(awss3Client).putObject(anyString(), anyString(), any(byte[].class), anyString());
////        when(fileService.createFile(any(FileRequest.class))).thenReturn(Mockito.mock(FileResponse.class));
//
//        MultipartFile file = Mockito.mock(MultipartFile.class);
//        when(file.getBytes()).thenThrow(IOException.class);
//
//        // when
//        UploadResponse response = uploadService.uploadImage("/image", file);
//
//        // then
//        assertNull(response);
//    }
}