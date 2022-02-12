package com.project.mentoridge.config.security.oauth.provider.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NaverResponse {

    private String resultcode;
    private String message;
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    class Response {

        private String email;
        private String nickname;
        private String profile_image;
        private String age;
        private String gender;
        private String id;
        private String name;
        private String birthday;
        private String birthyear;
        private String mobile;

    }
}
