package com.project.mentoridge.config.security.oauth.provider.google;

import com.project.mentoridge.config.security.oauth.provider.OAuthInfo;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;

import java.util.Map;

public class GoogleInfo implements OAuthInfo {

    private Map<String, Object> attributes;

    public GoogleInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public OAuthType getProvider() {
        return OAuthType.GOOGLE;
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
        return (String) attributes.get("picture");
    }
}
