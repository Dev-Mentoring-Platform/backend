package com.project.mentoridge.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AmazonS3Properties {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String getS3UploadUrl(String key) {
        StringBuilder sb = new StringBuilder();
        return sb.append("https://")
                 .append(bucket)
                 .append(".s3.")
                 .append(region)
                 .append(".amazonaws.com/")
                 .append(key)
                 .toString();
    }
}
