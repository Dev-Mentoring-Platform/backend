package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;

@ServiceTest
public class AmazonS3ClientTest {

//    @Autowired
//    private AWSS3Client awss3Client;
//
//    @Autowired
//    private AmazonS3Properties awss3Properties;
//
//    @Autowired
//    private AmazonS3Client amazonS3Client;
//
//    // @Test
//    void S3_업로드_테스트() throws Exception {
//        // given
//        File file = new ClassPathResource("image/test.png").getFile();
//        final String uuid = UUID.randomUUID().toString();
//        byte[] bytes = FileUtils.readFileToByteArray(file);
//        FileNameMap fileNameMap = URLConnection.getFileNameMap();
//        String contentType = fileNameMap.getContentTypeFor(file.getName());
//
//        // when
//        awss3Client.putObject(awss3Properties.getBucket(), uuid, bytes, contentType);
//
//        // then
//        S3Object s3Object = amazonS3Client.getObject(awss3Properties.getBucket(), uuid);
//        assertThat(s3Object).isNotNull();
//        assertThat(s3Object.getBucketName()).isEqualTo(awss3Properties.getBucket());
//        assertThat(s3Object.getKey()).isEqualTo(uuid);
//
//        // uuid 확인
//        System.out.println(awss3Properties.getS3UploadUrl(s3Object.getKey()));
//    }

}