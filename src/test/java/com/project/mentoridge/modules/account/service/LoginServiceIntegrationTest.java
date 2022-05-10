package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.getLoginRequestWithUsernameAndPassword;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class LoginServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String NICKNAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    JwtTokenManager jwtTokenManager;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;

//    @Test
//    void processLoginOAuth() {
//    }

//    @Test
//    void oauth() {
//    }

    // TODO - TEST
    @Disabled
    @Test
    void 회원가입_OAuth() {

        // Given
        // When
        Map<String, Object> attributes = new HashMap<>();
        Map<String, String> result = null;
                // loginService.signUpOAuth(new GoogleInfo(attributes));

        // Then
        // 유저 생성 확인
        // 이메일 verify 확인
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNotNull(user);
        assertTrue(user.isEmailVerified());
        System.out.println(String.format("provider : %s, providerId : %s", user.getProvider(), user.getProviderId()));

        // 멘티 생성 확인
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);

        // 로그인 확인 - jwt 토큰생성
        assertTrue(result.containsKey("header"));
        assertTrue(result.containsKey("token"));

    }

//    @Test
//    void loginOAuth() {
//    }

    // TODO - TEST
    @Disabled
    @Test
    void signUpOAuthDetail() {

        // Given
        Map<String, Object> attributes = new HashMap<>();
        Map<String, String> result = null;
        // loginService.signUpOAuth(new GoogleInfo(attributes));

        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        loginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertTrue(user.isEmailVerified());
        assertEquals(signUpOAuthDetailRequest.getPhoneNumber(), user.getPhoneNumber());
    }

    @Disabled
    @Test
    void 회원가입() {

        // Given
        // When
        loginService.signUp(signUpRequest);

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNull(user);

        User unverifiedUser = userRepository.findAllByUsername(USERNAME);
        assertAll(
                () -> assertNotNull(unverifiedUser),
                () -> assertFalse(unverifiedUser.isEmailVerified()),
                () -> assertEquals(RoleType.MENTEE, unverifiedUser.getRole()),
                () -> assertEquals(signUpRequest.getZone(), unverifiedUser.getZone().toString()),
                () -> assertEquals(signUpRequest.getPhoneNumber(), unverifiedUser.getPhoneNumber())
        );

        Mentee mentee = menteeRepository.findByUser(user);
        assertNull(mentee);
    }

    @Disabled
    @DisplayName("회원가입 - AlreadyExistException 발생")
    @Test
    void signUpWithExistingUsername() {

        // Given
        loginService.signUp(signUpRequest);
        assertNotNull(userRepository.findAllByUsername(USERNAME));

        // When
        assertThrows(AlreadyExistException.class, () -> {
            loginService.signUp(signUpRequest);
        });
    }

    @Disabled
    @Test
    void verifyEmail() {

        // Given
        User user = loginService.signUp(signUpRequest);
        assertFalse(user.isEmailVerified());
        assertFalse(userRepository.findByUsername(USERNAME).isPresent());
        assertNotNull(userRepository.findAllByUsername(USERNAME));

        // When
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNotNull(user);
        assertTrue(user.isEmailVerified());
    }

    @Disabled
    @DisplayName("회원 정보 추가 입력 - OAuth 가입이 아닌 경우")
    @Test
    void signUpOAuthDetail_notOAuthUser() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        User verifiedUser = userRepository.findByUsername(USERNAME).orElse(null);
        assertThrows(RuntimeException.class, () -> {
            loginService.signUpOAuthDetail(verifiedUser, signUpOAuthDetailRequest);
        });
    }

    @Disabled
    @Test
    void login() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        Map<String, String> result = loginService.login(loginRequest);

        // Then
        assertTrue(result.containsKey("header"));
        assertTrue(result.containsKey("token"));

        String jwtToken = result.get("token").replace("Bearer ", "");
        assertEquals(USERNAME, jwtTokenManager.getClaim(jwtToken, "username"));
    }

    // TODO - DisabledException
    @Disabled
    @Test
    void login_unverifiedUser() {

        // Given
        loginService.signUp(signUpRequest);

        // When
        // Then
        assertThrows(BadCredentialsException.class, () -> loginService.login(loginRequest));
    }

    @Disabled
    @Test
    void login_wrongPassword() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(USERNAME, "password_");
        assertThrows(BadCredentialsException.class, () -> loginService.login(loginRequest));
    }

//    @Test
//    void findPassword() {
//    }
}