package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.mail.EmailMessage;
import com.project.mentoridge.mail.EmailService;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    LoginService loginService;

    @Mock
    UserRepository userRepository;
    @Mock
    MenteeRepository menteeRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    JwtTokenManager jwtTokenManager;
    @Mock
    UserLogService userLogService;
    @Mock
    MenteeLogService menteeLogService;
    @Mock
    LoginLogService loginLogService;

    @Mock
    EmailService emailService;
    @Mock
    TemplateEngine templateEngine;

    @Test
    void checkUsernameDuplication() {
        // username

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByUsername("user@email.com")).thenReturn(user);

        // when
        boolean result = loginService.checkUsernameDuplication("user@email.com");
        // then
        assertTrue(result);
    }

    @Test
    void checkUsernameDuplication_withNoParam() {
        // username

        // given
        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> loginService.checkUsernameDuplication(""));
    }

    @Test
    void checkUsernameDuplication_duplicated() {
        // username
        // given
        when(userRepository.findAllByUsername("user@email.com")).thenReturn(null);

        // when
        boolean result = loginService.checkUsernameDuplication("user@email.com");
        // then
        assertFalse(result);
    }

    @Test
    void checkNicknameDuplication() {
        // nickname

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByNickname("user")).thenReturn(user);

        // when
        boolean result = loginService.checkNicknameDuplication("user");
        // then
        assertTrue(result);
    }

    @Test
    void checkNicknameDuplication_duplicated() {
        // nickname
        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByNickname("user")).thenReturn(null);

        // when
        boolean result = loginService.checkNicknameDuplication("user");
        // then
        assertFalse(result);
    }
/*
    @Test
    void signUp_checkSendEmail() {
        // signUpRequest

        // given
        when(userRepository.findAllByUsername("user@email.com")).thenReturn(null);

        User unverified = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(unverified);

        // when
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        when(signUpRequest.getUsername()).thenReturn("user@email.com");
        loginService.signUp(signUpRequest);

        // then
        verify(userRepository).save(any(User.class));
        verify(userLogService).insert(any(User.class), any(User.class));
        // this error might show up because you verify either of: final/private/equals()/hashCode() methods
        verify(templateEngine, atLeastOnce()).process("verify-email", any(Context.class));
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }*/

    @Test
    void signUp_existUsername() {
        // signUpRequest

        // given
        User existed = mock(User.class);
        when(userRepository.findAllByUsername("user@email.com")).thenReturn(existed);

        // when
        // then
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        when(signUpRequest.getUsername()).thenReturn("user@email.com");
        assertThrows(AlreadyExistException.class,
                () -> loginService.signUp(signUpRequest));
    }

    @Test
    void verifyEmail() {
        // email(username), token

        // given
        User user = mock(User.class);
        when(userRepository.findUnverifiedUserByUsername("user@email.com")).thenReturn(Optional.of(user));
        when(user.getEmailVerifyToken()).thenReturn("token");

        Mentee saved = mock(Mentee.class);
        when(menteeRepository.save(any(Mentee.class))).thenReturn(saved);

        // when
        loginService.verifyEmail("user@email.com", "token");

        // then
        verify(user).verifyEmail(userLogService);
        verify(menteeRepository).save(any(Mentee.class));
        verify(menteeLogService).insert(user, saved);
    }
/*
    @DisplayName("존재하지 않는 사용자")
    @Test
    void verifyEmail_notExistUser() {

        // given
        when(userRepository.findUnverifiedUserByUsername("user@email.com")).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail("user@email.com", "token"));

    }*/

    @DisplayName("이미 인증된 사용자")
    @Test
    void verifyEmail_alreadyVerifiedUser() {

        // given
        when(userRepository.findUnverifiedUserByUsername("user@email.com")).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail("user@email.com", "token"));
    }

    @DisplayName("해당 사용자의 토큰이 아닌 경우")
    @Test
    void verifyEmail_wrongToken() {

        // given
        // given
        User user = mock(User.class);
        when(userRepository.findUnverifiedUserByUsername("user@email.com")).thenReturn(Optional.of(user));
        when(user.getEmailVerifyToken()).thenReturn("token");

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail("user@email.com", "wrong_token")
        );
    }

    @Test
    void login() {
        // username, password

        // given
        String username = "user@email.com";
        String password = "password";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);

        Authentication authentication = mock(Authentication.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        when(authentication.getPrincipal()).thenReturn(principalDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String accessToken = "accessToken";
        when(jwtTokenManager.createToken(eq("user@email.com"), any(Map.class))).thenReturn(accessToken);
        String refreshToken = "refreshToken";
        when(jwtTokenManager.createRefreshToken()).thenReturn(refreshToken);

        // when
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
        loginService.login(loginRequest);

        // then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        // jwt
        // 1. access-token
        verify(jwtTokenManager).createToken(eq("user@email.com"), any(Map.class));
        // 2. refresh-token
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(refreshToken);

        verify(user).login(loginLogService);
        // verify(loginLogService).login(user);
        verify(jwtTokenManager).getJwtTokens(accessToken, refreshToken);
    }

    @Test
    void refreshToken_when_accessToken_is_not_expired() {

        // given
        String accessToken = "accessToken";
        // accessToken 만료 X
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(true);

        // when
        JwtTokenManager.JwtResponse response = loginService.refreshToken("Bearer accessToken", "Bearer refreshToken", "ROLE_MENTEE");
        // then
        assertNull(response);
    }

    @Test
    void refreshToken_when_accessToken_is_expired() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));

        String newAccessToken = "newAccessToken";
        when(jwtTokenManager.createToken(eq("user@email.com"), any(Map.class))).thenReturn(newAccessToken);
        // refreshToken 만료 X
        when(jwtTokenManager.verifyToken(refreshToken)).thenReturn(true);

        // when
        JwtTokenManager.JwtResponse result = loginService.refreshToken("Bearer accessToken", "Bearer refreshToken", "ROLE_MENTEE");

        // then
        // accessToken 생성
        verify(jwtTokenManager).createToken(eq("user@email.com"), any(Map.class));
        verify(jwtTokenManager).getJwtTokens(newAccessToken, refreshToken);
    }

    @Test
    void refreshToken_when_refreshToken_is_not_in_database() {

        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);
        // refreshToken
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loginService.refreshToken("Bearer accessToken", "Bearer refreshToken", "ROLE_MENTEE"));
    }

    @DisplayName("refreshToken도 만료된 경우")
    @Test
    void refreshToken_when_all_tokens_are_expired() {

        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));

        String newAccessToken = "newAccessToken";
        when(jwtTokenManager.createToken(eq("user@email.com"), any(Map.class))).thenReturn(newAccessToken);

        // refreshToken 만료
        when(jwtTokenManager.verifyToken(refreshToken)).thenReturn(false);
        String newRefreshToken = "newRefreshToken";
        when(jwtTokenManager.createRefreshToken()).thenReturn(newRefreshToken);

        // when
        JwtTokenManager.JwtResponse result
                = loginService.refreshToken("Bearer accessToken", "Bearer refreshToken", "ROLE_MENTEE");

        // then
        // accessToken 생성
        verify(jwtTokenManager).createToken(eq("user@email.com"), any(Map.class));
        // refresh-token 생성
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(newRefreshToken);
        verify(jwtTokenManager).getJwtTokens(newAccessToken, newRefreshToken);
    }
/*
    @Test
    void findPassword() {
        // 랜덤 비밀번호 생성 후 메일로 전송
        // username

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));
        String randomPassword = "randomPassword";
        when(user.findPassword(bCryptPasswordEncoder, userLogService)).thenReturn(randomPassword);

        // when
        loginService.findPassword("user@email.com");

        // then
        verify(templateEngine).process(eq("find-password"), any(Context.class));
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }*/

    @Test
    void change_type_when_role_is_mentee() {

        // given
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        String accessToken = "accessToken";
        when(jwtTokenManager.createToken(eq("user@email.com"), any(Map.class))).thenReturn(accessToken);
        String refreshToken = "refreshToken";
        when(jwtTokenManager.createRefreshToken()).thenReturn(refreshToken);

        // when
        loginService.changeType("user@email.com", "ROLE_MENTEE");

        // then
        verify(jwtTokenManager).createToken(eq("user@email.com"), any(Map.class));
        verify(jwtTokenManager).createRefreshToken();

        verify(user).updateRefreshToken(refreshToken);
        verify(jwtTokenManager).getJwtTokens(anyString(), anyString());
    }

    @Test
    void change_type_when_role_is_mentee_but_cannot_change_to_mentor() {

        // given
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.MENTEE);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // when
        assertThrows(UnauthorizedException.class,
                () -> loginService.changeType("user@email.com", "ROLE_MENTEE"));
    }

    @Test
    void change_type_when_role_is_mentor() {

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        String accessToken = "accessToken";
        when(jwtTokenManager.createToken(eq("user@email.com"), any(Map.class))).thenReturn(accessToken);
        String refreshToken = "refreshToken";
        when(jwtTokenManager.createRefreshToken()).thenReturn(refreshToken);

        // when
        loginService.changeType("user@email.com", "ROLE_MENTOR");

        // then
        verify(user).updateRefreshToken(refreshToken);
        verify(jwtTokenManager).getJwtTokens(accessToken, refreshToken);
    }

}