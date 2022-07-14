package com.project.mentoridge.config.security.oauth;

import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private OAuthType provider;
    private String providerId;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    private String name;
    private String email;
    private String picture;

    @Builder(access = AccessLevel.PRIVATE)
    private OAuthAttributes(OAuthType provider, String providerId, Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.provider = provider;
        this.providerId = providerId;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;

        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(CustomOAuth2User oAuth2User) {
        return of(oAuth2User.getRegistrationId(), oAuth2User.getUserNameAttributeKey(), oAuth2User.getAttributes());
    }

    public static OAuthAttributes of(String registrationId, String nameAttributeKey, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(OAuthType.NAVER.name())) {
            return ofNaver("id", attributes);
        } else if (registrationId.equalsIgnoreCase(OAuthType.KAKAO.name())) {
            return ofKakao("id", attributes);
        } else if (registrationId.equalsIgnoreCase(OAuthType.GOOGLE.name())) {
            return ofGoogle(nameAttributeKey, attributes);
        }
        throw new OAuth2AuthenticationException("Not Supported OAuthType");
    }

    // /oauth2/authorization/google
    private static OAuthAttributes ofGoogle(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(nameAttributeKey)
                .provider(OAuthType.GOOGLE)
                .providerId((String) attributes.get(nameAttributeKey))
                .build();
    }

    // 리디렉션 URL : /login/oauth2/code/naver
    // /oauth2/authorization/naver
    private static OAuthAttributes ofNaver(String nameAttributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(nameAttributeKey)
                .provider(OAuthType.NAVER)
                .providerId((String) response.get(nameAttributeKey))
                .build();
    }

    // /oauth2/authorization/kakao
    private static OAuthAttributes ofKakao(String nameAttributeKey, Map<String, Object> attributes) {

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return OAuthAttributes.builder()
                .name((String) properties.get("nickname"))
                .picture((String) properties.get("profile_image"))
                .email((String) kakaoAccount.get("email"))
                .attributes(properties)
                .nameAttributeKey(nameAttributeKey)
                .provider(OAuthType.KAKAO)
                .providerId(String.valueOf(attributes.get(nameAttributeKey)))
                .build();
    }
}
