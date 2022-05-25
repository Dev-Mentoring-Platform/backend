package com.project.mentoridge.config.security.oauth;

import com.project.mentoridge.modules.account.enums.RoleType;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Data
public class CustomOAuth2User implements OAuth2User {

    private final String registrationId;
    private final String userNameAttributeKey;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(String registrationId, String userNameAttributeKey, Map<String, Object> attributes) {
        this.registrationId = registrationId;
        this.userNameAttributeKey = userNameAttributeKey;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(RoleType.MENTEE.getType()));
    }

    @Override
    public String getName() {
        return this.userNameAttributeKey;
    }
}
