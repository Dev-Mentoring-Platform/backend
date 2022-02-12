package com.project.mentoridge.config.security.oauth.provider.naver;

import com.project.mentoridge.config.security.oauth.provider.OAuthInfo;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;

public class NaverInfo implements OAuthInfo {

    private NaverResponse userInfo;

    public NaverInfo(NaverResponse userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getProviderId() {
        return userInfo.getResponse().getId();
    }

    @Override
    public OAuthType getProvider() {
        return OAuthType.NAVER;
    }

    @Override
    public String getName() {
        return userInfo.getResponse().getName();
    }

    @Override
    public String getEmail() {
        return userInfo.getResponse().getEmail();
    }
}
