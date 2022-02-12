package com.project.mentoridge.config.security.oauth.provider;

public interface OAuthInfo {

    static OAuthType getOAuthType(String provider) {

        switch (provider) {
            case "google":
                return OAuthType.GOOGLE;
            case "naver":
                return OAuthType.NAVER;
            case "kakao":
                return OAuthType.KAKAO;
            default:
                return null;
        }
    }

    String getProviderId();
    OAuthType getProvider();
    String getName();
    String getEmail();

}
