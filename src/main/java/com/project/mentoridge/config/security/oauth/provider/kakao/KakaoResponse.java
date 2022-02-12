package com.project.mentoridge.config.security.oauth.provider.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KakaoResponse {

    private Long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    class Properties {
        private String nickname;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    class KakaoAccount {

        // @JsonProperty("profile_nickname_needs_agreement")
        private boolean profile_nickname_needs_agreement;
        private Profile profile;
        private boolean email_needs_agreement;
        private String email;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        class Profile {
            private String nickname;
        }
    }

}
