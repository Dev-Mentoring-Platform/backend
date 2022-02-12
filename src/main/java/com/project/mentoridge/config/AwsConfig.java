package com.project.mentoridge.config;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

//    @Value("${cloud.aws.credentials.accessKey}")
//    private String ACCESS_KEY;
//
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String SECRET_KEY;

    @Bean
    public AmazonS3 getAmazonS3() {
//        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
//        return AmazonS3ClientBuilder.standard()
//                                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                                    .withRegion(Regions.AP_NORTHEAST_2)
//                                    .build();
        return null;
    }

}
