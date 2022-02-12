package com.project.mentoridge.config.security.oauth.provider.google;

import com.project.mentoridge.config.security.oauth.provider.OAuthInfo;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;

import java.util.Map;

public class GoogleInfo implements OAuthInfo {

    private Map<String, String> userInfo;

    public GoogleInfo(Map<String, String> userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getProviderId() {
        return userInfo.get("id");
    }

    @Override
    public OAuthType getProvider() {
        return OAuthType.GOOGLE;
    }

    @Override
    public String getName() {
        return userInfo.get("name");
    }

    @Override
    public String getEmail() {
        return userInfo.get("email");
    }
}
