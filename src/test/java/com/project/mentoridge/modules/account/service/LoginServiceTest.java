package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.mail.EmailMessage;
import com.project.mentoridge.mail.EmailService;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    JwtTokenManager jwtTokenManager;
    @Mock
    AuthenticationManager authenticationManager;

    @Spy
    EmailService emailService;
    @Spy
    TemplateEngine templateEngine = new SpringTemplateEngine();

    @BeforeEach
    void setup() {
        assertNotNull(loginService);
    }

    @Test
    void checkUsernameDuplication() {
        // username

        // given
        String username = "user1@email.com";
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByUsername(username)).thenReturn(user);

        // when
        boolean result = loginService.checkUsernameDuplication(username);
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
        String username = "user1@email.com";
        when(userRepository.findAllByUsername(username)).thenReturn(null);

        // when
        boolean result = loginService.checkUsernameDuplication(username);
        // then
        assertFalse(result);
    }

    @Test
    void checkNicknameDuplication() {
        // nickname

        // given
        String nickname = "user1";
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByNickname(nickname)).thenReturn(user);

        // when
        boolean result = loginService.checkNicknameDuplication(nickname);
        // then
        assertTrue(result);
    }

    @Test
    void checkNicknameDuplication_duplicated() {
        // nickname

        // given
        String nickname = "user1";
        User user = Mockito.mock(User.class);
        when(userRepository.findAllByNickname(nickname)).thenReturn(null);

        // when
        boolean result = loginService.checkNicknameDuplication(nickname);
        // then
        assertFalse(result);
    }

    @Test
    void signUp_checkSendEmail() {
        // signUpRequest

        // given
        when(userRepository.findAllByUsername(anyString())).thenReturn(null);
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        // when
        String username = "user1@email.com";
        SignUpRequest signUpRequest = SignUpRequest.of(
                username,
                "password",
                "password",
                "user1",
                "FEMALE",
                null,
                null,
                null,
                "user1",
                null,
                "서울특별시 강남구 삼성동",
                null
        );
        loginService.signUp(signUpRequest);

        // then
        // this error might show up because you verify either of: final/private/equals()/hashCode() methods
        // verify(templateEngine, atLeastOnce()).process(anyString(), new Context());
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }

    @Test
    void signUp_existUsername() {
        // signUpRequest

        // given
        String username = "user1@email.com";
        when(userRepository.findAllByUsername(username)).thenReturn(Mockito.mock(User.class));

        // when
        // then
        SignUpRequest signUpRequest = SignUpRequest.of(
                username,
                "password",
                "password",
                "user1",
                "FEMALE",
                null,
                null,
                null,
                "user1",
                null,
                "서울특별시 강남구 삼성동",
                null
        );

        assertThrows(AlreadyExistException.class,
                () -> loginService.signUp(signUpRequest));
    }

    @Test
    void verifyEmail() {
        // email(username), token

        // given
        String email = "user1@email.com";

        User user = User.of(email, null, null, null, null, null,
                email, null, null, null, null, null, null, null);
        user.generateEmailVerifyToken();
        assert !user.isEmailVerified();
        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.of(user));

        // when
        loginService.verifyEmail(email, user.getEmailVerifyToken());

        // then
        assertTrue(user.isEmailVerified());
    }

    @DisplayName("존재하지 않는 사용자")
    @Test
    void verifyEmail_notExistUser() {

        // given
        String email = "user1@email.com";
        String token = "token";
        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail(email, token));

    }

    @DisplayName("이미 인증된 사용자")
    @Test
    void verifyEmail_alreadyVerifiedUser() {

        // given
        String email = "user1@email.com";

        User user = User.of(email, null, null, null, null, null,
                email, null, null, null, null, null, null, null);
        user.generateEmailVerifyToken();

        user.verifyEmail();
        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.of(user));

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail(email, user.getEmailVerifyToken())
        );

    }


    @Test
    void findPassword() {
        // 랜덤 비밀번호 생성 후 메일로 전송
        // username

        // given
        String username = "user1@email.com";
        String password = "password";
        String randomPassword = "randomPassword";
        User user = User.of(username, password, null, null, null, null,
                null, null, null, null, null, null, null, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(randomPassword);
        // when
        loginService.findPassword(username);

        // then
        // 1. 랜덤 비밀번호 생성 - passwordEncoder
        verify(bCryptPasswordEncoder, atLeastOnce()).encode(anyString());
        // 2. user에 set
        // 3. 메일로 전송
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }

    // TODO - 로그인 테스트
//    @Test
//    void login() {
//        // username, password
//
//        // given
//
//        // when
//        // then
//    }
}