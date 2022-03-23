package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.config.AmazonS3Properties;
import com.project.mentoridge.modules.upload.amazon.service.AWSS3Client;
import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UploadServiceImpl implements UploadService {

    private final AmazonS3Properties amazonS3Properties;
    private final AWSS3Client awsS3Client;
    private final FileService fileService;

    @Override
    public UploadResponse uploadImage(String dir, MultipartFile file) {

        try {

            String uuid = UUID.randomUUID().toString();

            String key = uuid;
            if (!StringUtils.isBlank(dir)) {
                key = dir + "/" + uuid;
            }
            awsS3Client.putObject(amazonS3Properties.getBucket(), key, file.getBytes(), file.getContentType());

            FileRequest fileRequest = FileRequest.builder()
                    .uuid(uuid)
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .type(FileType.LECTURE_IMAGE)
                    .size(file.getSize())
                    .build();
            FileResponse fileResponse = fileService.createFile(fileRequest);
            return new UploadResponse(fileResponse, amazonS3Properties.getS3UploadUrl(key));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
