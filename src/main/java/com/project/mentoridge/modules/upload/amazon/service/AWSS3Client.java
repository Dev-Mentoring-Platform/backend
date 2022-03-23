package com.project.mentoridge.modules.upload.amazon.service;

import com.project.mentoridge.config.exception.AmazonS3Exception;

public interface AWSS3Client {

    void putObject(String bucket, String key, byte[] bytes, String contentType) throws AmazonS3Exception;
    void deleteObject(String bucket, String key) throws AmazonS3Exception;
}
