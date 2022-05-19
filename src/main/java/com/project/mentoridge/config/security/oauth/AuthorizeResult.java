package com.project.mentoridge.config.security.oauth;

import lombok.Data;

@Data
public class AuthorizeResult {

    private String idToken;
    private String[] scopes;
    private String serverAuthCode;
    private UserInfo user;

    // TODO - CHECK : public
    @Data
    public static class UserInfo {

        private String email;
        private String familyName;
        private String givenName;
        private String id;
        private String name;
        private String photo;
    }
}
