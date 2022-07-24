package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.config.security.oauth.CustomOAuth2SuccessHandler;
import com.project.mentoridge.config.security.oauth.CustomOAuth2User;
import com.project.mentoridge.config.security.oauth.CustomOAuth2UserService;
import com.project.mentoridge.config.security.oauth.OAuthAttributes;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    @InjectMocks
    OAuthLoginService oAuthLoginService;
    @Mock
    UserRepository userRepository;
    @Mock
    MenteeRepository menteeRepository;
    @Mock
    JwtTokenManager jwtTokenManager;

    @Mock
    LoginLogService loginLogService;
    @Mock
    MenteeLogService menteeLogService;
    @Mock
    UserLogService userLogService;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;
    @InjectMocks
    CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    private String name = "user";
    private String email = "user@email.com";
    private String registrationId;
    private String nameAttributeKey;

    private Map<String, Object> attributes;
    private OAuthAttributes oAuthAttributes;

    @BeforeEach
    void init() {

        registrationId = "Naver";
        nameAttributeKey = "id";

        attributes = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        response.put("name", "user");
        response.put("email", "user@email.com");
        response.put("picture", null);
        response.put(nameAttributeKey, "providerId");
        attributes.put("response", response);
        oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
    }

    @Test
    void save_when_existed_email() {

        // given
        OAuthAttributes attributes = mock(OAuthAttributes.class);
        when(attributes.getEmail()).thenReturn(email);
        // 동일 계정 존재
        User existed = mock(User.class);
        when(userRepository.findAllByUsername(email)).thenReturn(existed);

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.save(attributes));
    }

    @Test
    void save() {

        // given
        // 동일 계정 존재 X
        when(userRepository.findAllByUsername(email)).thenReturn(null);
        when(userRepository.countAllByNickname(name)).thenReturn(0);

        // when
        oAuthLoginService.save(oAuthAttributes);

        // then
        verify(menteeRepository).save(any(Mentee.class));
        verify(menteeLogService).insert(any(User.class), any(Mentee.class));
        // TODO - verifyEmail
    }

    @Test
    void save_when_existed_nickname() {
        // 닉네임(name) +1로 임시 처리 => 통과

        // given
        // 동일 계정 존재 X
        when(userRepository.findAllByUsername(email)).thenReturn(null);
        when(userRepository.countAllByNickname(name)).thenReturn(1);

        // when
        oAuthLoginService.save(oAuthAttributes);

        // then
        verify(menteeRepository).save(any(Mentee.class));
        verify(menteeLogService).insert(any(User.class), any(Mentee.class));
        // TODO - verifyEmail
    }

    // TODO
    /*
    Cannot mock/spy class org.springframework.security.oauth2.client.registration.ClientRegistration
    Mockito cannot mock/spy because :
    - final class
    */
    @Disabled
    @Test
    void load_user_return_CustomOAuth2User() {
/*
        // given
        // when
        OAuth2UserRequest oAuth2UserRequest = mock(OAuth2UserRequest.class);
        when(oAuth2UserRequest.getClientRegistration()).thenReturn(mock(ClientRegistration.class));
        when(oAuth2UserRequest.getClientRegistration().getRegistrationId()).thenReturn("Naver");
        when(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).thenReturn("id");
        OAuth2User oAuth2User = customOAuth2UserService.loadUser(oAuth2UserRequest);

        // then
        assertNotNull(oAuth2User);
        assertTrue(oAuth2User instanceof CustomOAuth2User);
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) oAuth2User;
        assertEquals("Naver", customOAuth2User.getRegistrationId());
        assertEquals("id", customOAuth2User.getUserNameAttributeKey());*/
    }

    // TODO - TEST
    @Test
    void login_success() throws ServletException, IOException {

        // given
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(registrationId, nameAttributeKey, attributes);
        // when(customOAuth2UserService.loadUser(any(OAuth2UserRequest.class))).thenReturn(customOAuth2User);
        User user = mock(User.class);
        when(userRepository.findByProviderAndProviderId(OAuthType.NAVER, "providerId")).thenReturn(user);
        // when
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        customOAuth2SuccessHandler.onAuthenticationSuccess(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class), eq(authentication));

        // then
        verify(userRepository).findByProviderAndProviderId(OAuthType.NAVER, "providerId");
        verify(user).update(any(OAuthAttributes.class), userLogService);
        verify(oAuthLoginService).loginOAuth("user@email.com");

        // TODO - TEST : redirect
    }

    @Test
    void oauth_detail_not_oauth() {

        // given
        // when
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getProvider()).thenReturn(null);
        // when(user.getProviderId()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // then
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = mock(SignUpOAuthDetailRequest.class);
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest));
    }

    @Test
    void oauth_detail_existed_nickname() {

        // given
        // when
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getProvider()).thenReturn(OAuthType.NAVER);
        when(user.getProviderId()).thenReturn("providerId");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findAllByNickname("nickname")).thenReturn(mock(User.class));

        SignUpOAuthDetailRequest signUpOAuthDetailRequest = mock(SignUpOAuthDetailRequest.class);
        when(signUpOAuthDetailRequest.getNickname()).thenReturn("nickname");
        // then
        assertThrows(AlreadyExistException.class,
                () -> oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest));
    }

    @Test
    void oauth_detail() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getProvider()).thenReturn(OAuthType.NAVER);
        when(user.getProviderId()).thenReturn("providerId");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findAllByNickname("nickname")).thenReturn(null);

        // when
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = mock(SignUpOAuthDetailRequest.class);
        when(signUpOAuthDetailRequest.getNickname()).thenReturn("nickname");
        oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);

        // then
        verify(user).updateOAuthDetail(signUpOAuthDetailRequest, userLogService);
    }

}