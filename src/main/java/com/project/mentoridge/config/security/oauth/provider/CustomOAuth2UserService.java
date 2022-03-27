package com.project.mentoridge.config.security.oauth.provider;

import com.project.mentoridge.config.security.SessionUser;
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

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    // private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MenteeRepository menteeRepository;
    // private final HttpSession httpSession;

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
            if ("naver".equalsIgnoreCase(registrationId)) {
                // return ofNaver("id", attributes);
                return null;
            }
            return ofGoogle(nameAttributeName, attributes);
        }

        public static OAuthAttributes ofGoogle(String nameAttributeName, Map<String, Object> attributes) {
            return OAuthAttributes.builder()
                    .name((String) attributes.get("name"))
                    .email((String) attributes.get("email"))
                    .picture((String) attributes.get("picture"))
                    .attributes(attributes)
                    .nameAttributeKey(nameAttributeName)
                    .build();
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        // 서비스 구분 코드 : 구글 / 네이버 / 카카오
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2User에서 반환하는 사용자 정보는 Map
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = save(attributes);
        // 세션 생성
        // httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getType())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }
}
