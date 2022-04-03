package com.project.mentoridge.config.security.oauth.provider.naver;

import com.project.mentoridge.config.security.oauth.provider.OAuthInfo;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;

import java.util.Map;

public class NaverInfo implements OAuthInfo {

    // private NaverResponse userInfo;
    private Map<String, Object> attributes;

    public NaverInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public OAuthType getProvider() {
        return OAuthType.NAVER;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image");
    }
}
