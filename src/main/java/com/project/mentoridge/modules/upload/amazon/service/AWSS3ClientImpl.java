package com.project.mentoridge.modules.upload.amazon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AWSS3ClientImpl implements AWSS3Client {

//    private final AmazonS3 amazonS3;
//
//    @Override
//    public void putObject(String bucket, String key, byte[] bytes, String contentType) throws AmazonS3Exception {
//
//        try (InputStream is = new ByteArrayInputStream(bytes)) {
//
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentType(contentType);
//            objectMetadata.setContentLength(bytes.length);
//
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, is, objectMetadata);
//            amazonS3.putObject(putObjectRequest);
//
//        } catch (Exception e) {
//            // TODO - 로그 포맷
//            log.error(ExceptionUtils.getMessage(e));
//            throw new AmazonS3Exception(AmazonS3Exception.UPLOAD, e);
//        }
//    }
//
//    @Override
//    public void deleteObject(String bucket, String key) throws AmazonS3Exception {
//
//        try {
//            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
//            amazonS3.deleteObject(deleteObjectRequest);
//        } catch (Exception e) {
//            log.error(ExceptionUtils.getMessage(e));
//            throw new AmazonS3Exception(AmazonS3Exception.DELETE, e);
//        }
//    }

}
