package com.project.mentoridge.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.accessKey}")
    private String ACCESS_KEY;

    @Value("${cloud.aws.credentials.secretKey}")
    private String SECRET_KEY;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        return AmazonS3ClientBuilder.standard()
                                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                                    .withRegion(Regions.AP_NORTHEAST_2)
                                    .build();
    }

}
