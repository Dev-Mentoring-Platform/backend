package com.project.mentoridge.config.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
// 로그인 이후 가져온 사용자의 정보들을 기반으로 가입, 정보 수정, 세션 저장 등의 기능 지원

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        // TODO - CHECK : delegate
        // OAuth2 공급자로부터 Access Token을 받은 이후 호출
        // OAuth2 공급자로부터 사용자 정보를 가져온다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        // 서비스 구분 코드 : 구글 / 네이버 / 카카오
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeKey = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2User에서 반환하는 사용자 정보는 Map
        return new CustomOAuth2User(registrationId, userNameAttributeKey, oAuth2User.getAttributes());
        // 인증/인가를 세션 방식으로 구현하면 return한 OAuth2User 객체가 시큐리티 세션에 저장된다.
        // JWT 방식으로 구현할 경우 세션을 사용하지 않으므로 세션에 저장하지는 않는다.
//        return new PrincipalDetails(user, oAuth2User.getAttributes(),
//                Collections.singletonList(new SimpleGrantedAuthority(RoleType.MENTEE.getType())));
    }
}
