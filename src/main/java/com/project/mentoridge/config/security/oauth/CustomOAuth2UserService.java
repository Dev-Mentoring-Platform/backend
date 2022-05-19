package com.project.mentoridge.config.security.oauth;

import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
// 로그인 이후 가져온 사용자의 정보들을 기반으로 가입, 정보 수정, 세션 저장 등의 기능 지원
    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        // TODO - CHECK : delegate
        // OAuth2 공급자로부터 Access Token을 받은 이후 호출
        // OAuth2 공급자로부터 사용자 정보를 가져온다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        // 서비스 구분 코드 : 구글 / 네이버 / 카카오
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println(registrationId + " " + userNameAttributeName);
        // OAuth2User에서 반환하는 사용자 정보는 Map
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        System.out.println(oAuth2User.getAttributes());
        User user = save(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(RoleType.MENTEE.getType())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
        // 인증/인가를 세션 방식으로 구현하면 return한 OAuth2User 객체가 시큐리티 세션에 저장된다.
        // JWT 방식으로 구현할 경우 세션을 사용하지 않으므로 세션에 저장하지는 않는다.
        // return new PrincipalDetails(user, oAuth2User.getAttributes());
    }


    @Getter
    static class OAuthAttributes {

        private Map<String, Object> attributes;
        private String nameAttributeKey;
        private String name;
        private String email;
        private String picture;

        @Builder(access = AccessLevel.PRIVATE)
        public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
            this.attributes = attributes;
            this.nameAttributeKey = nameAttributeKey;
            this.name = name;
            this.email = email;
            this.picture = picture;
        }

        public static OAuthAttributes of(String registrationId, String nameAttributeName, Map<String, Object> attributes) {
            if (registrationId.equalsIgnoreCase(OAuthType.NAVER.name())) {
                return ofNaver("id", attributes);
            } else if (registrationId.equalsIgnoreCase(OAuthType.KAKAO.name())) {
                return ofKakao("id", attributes);
            } else if (registrationId.equalsIgnoreCase(OAuthType.GOOGLE.name())) {
                return ofGoogle(nameAttributeName, attributes);
            }
            throw new OAuth2AuthenticationException("Not Supported OAuthType");
        }

        // /oauth2/authorization/google
        public static OAuthAttributes ofGoogle(String nameAttributeName, Map<String, Object> attributes) {
            return OAuthAttributes.builder()
                    .name((String) attributes.get("name"))
                    .email((String) attributes.get("email"))
                    .picture((String) attributes.get("picture"))
                    .attributes(attributes)
                    .nameAttributeKey(nameAttributeName)
                    .build();
        }

        // 리디렉션 URL : /login/oauth2/code/naver
        // /oauth2/authorization/naver
        public static OAuthAttributes ofNaver(String nameAttributeName, Map<String, Object> attributes) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return OAuthAttributes.builder()
                    .name((String) response.get("name"))
                    .email((String) response.get("email"))
                    .picture((String) response.get("profile_image"))
                    .attributes(response)
                    .nameAttributeKey(nameAttributeName)
                    .build();
        }

        public static OAuthAttributes ofKakao(String nameAttributeName, Map<String, Object> attributes) {
            return null;
        }
    }

    private User save(OAuthAttributes attributes) {

        String username = attributes.getEmail();
        User user = userRepository.findByUsername(username)
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(User.builder()
                        .username(username)
                        .password(username)
                        .name(attributes.getName())
                        .gender(null)
                        .birthYear(null)
                        .phoneNumber(null)
                        .nickname(attributes.getName())
                        .zone(null)
                        .image(attributes.getPicture())
                        .role(RoleType.MENTEE)
                        .provider(null)
                        .providerId(null)
                        .build());
        // CascadeType.PERSIST로 중복 저장
        // User saved = userRepository.save(user);
        user.verifyEmail();

        Mentee mentee = Mentee.builder()
                .user(user)
                .build();
        menteeRepository.save(mentee);
        return user;
    }
}
