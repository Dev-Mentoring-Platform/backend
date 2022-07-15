package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
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
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    MenteeLogService menteeLogService;
    @Mock
    UserLogService userLogService;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;
    @InjectMocks
    CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Test
    void save_when_existed_email() {

        // given
        String name = "user";
        String email = "user@email.com";
        // 동일 계정 존재
        User existed = mock(User.class);
        when(userRepository.findAllByUsername(email)).thenReturn(existed);

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.save(any(OAuthAttributes.class)));
    }

    @Test
    void save() {

        // given
        String name = "user";
        String email = "user@email.com";
        // 동일 계정 존재 X
        when(userRepository.findAllByUsername(email)).thenReturn(null);
        when(userRepository.countAllByNickname(name)).thenReturn(0);

        // when
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");

        User user = oAuthLoginService.save(OAuthAttributes.of("Naver", nameAttributeKey, attributes));

        // then
        verify(menteeRepository).save(any(Mentee.class));
        verify(menteeLogService).insert(user, any(Mentee.class));
        verify(user).verifyEmail(userLogService);
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(email, user.getUsername()),
                () -> assertEquals(name, user.getNickname()),
                () -> assertEquals(attributes.get("picture"), user.getImage())
        );
    }

    // TODO - TEST
    @Test
    void save_when_existed_nickname() {
        // 닉네임(name) +1로 임시 처리

        // given
        String name = "user";
        String email = "user@email.com";
        // 동일 계정 존재 X
        when(userRepository.findAllByUsername(email)).thenReturn(null);
        when(userRepository.countAllByNickname(name)).thenReturn(1);

        // when
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");

        User user = oAuthLoginService.save(OAuthAttributes.of("Naver", nameAttributeKey, attributes));

        // then
        verify(menteeRepository).save(any(Mentee.class));
        verify(menteeLogService).insert(user, any(Mentee.class));
        verify(user).verifyEmail(userLogService);
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(email, user.getUsername()),
                () -> assertEquals(name + "2", user.getNickname()),
                () -> assertEquals(attributes.get("picture"), user.getImage())
        );

    }

    @Test
    void load_user_return_CustomOAuth2User() {

        // given
        // when
        OAuth2UserRequest oAuth2UserRequest = mock(OAuth2UserRequest.class);
        when(oAuth2UserRequest.getClientRegistration()).thenReturn(mock(ClientRegistration.class));
        when(oAuth2UserRequest.getClientRegistration().getRegistrationId()).thenReturn("Naver");
        when(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).thenReturn("id");
        OAuth2User oAuth2User = customOAuth2UserService.loadUser(oAuth2UserRequest);

        // TODO - how to test delegate?

        // then
        assertNotNull(oAuth2User);
        assertTrue(oAuth2User instanceof CustomOAuth2User);
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) oAuth2User;
        assertEquals("Naver", customOAuth2User.getRegistrationId());
        assertEquals("id", customOAuth2User.getUserNameAttributeKey());
    }

    // TODO - TEST
    @Test
    void login_success() throws ServletException, IOException {

        // given
        String registrationId = "Naver";
        String userNameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "user@email.com");
        attributes.put("picture", null);
        attributes.put(userNameAttributeKey, "providerId");

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(registrationId, userNameAttributeKey, attributes);
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
        when(user.getProviderId()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // then
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.signUpOAuthDetail(user, any(SignUpOAuthDetailRequest.class)));
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
        // when
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getProvider()).thenReturn(OAuthType.NAVER);
        when(user.getProviderId()).thenReturn("providerId");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findAllByNickname("nickname")).thenReturn(null);

        SignUpOAuthDetailRequest signUpOAuthDetailRequest = mock(SignUpOAuthDetailRequest.class);
        when(signUpOAuthDetailRequest.getNickname()).thenReturn("nickname");
        oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);

        // then
        verify(user).updateOAuthDetail(signUpOAuthDetailRequest, userLogService);
    }

}