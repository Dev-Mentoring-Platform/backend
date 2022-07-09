package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.mail.EmailMessage;
import com.project.mentoridge.mail.EmailService;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
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
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    JwtTokenManager jwtTokenManager;
    @Mock
    UserLogService userLogService;
    @Mock
    MenteeLogService menteeLogService;
    @Mock
    LoginLogService loginLogService;

    @Spy
    EmailService emailService;

    @BeforeEach
    void init() {
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
        SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname("user1", "user1");
        User user = loginService.signUp(signUpRequest);

        // then
        verify(userLogService).insert(user, user);
        // this error might show up because you verify either of: final/private/equals()/hashCode() methods
        // verify(templateEngine, atLeastOnce()).process(anyString(), new Context());
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }

    @Test
    void signUp_existUsername() {
        // signUpRequest

        // given
        String name = "user1";
        when(userRepository.findAllByUsername(name + "@email.com")).thenReturn(Mockito.mock(User.class));

        // when
        // then
        SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname(name, name);
        assertThrows(AlreadyExistException.class,
                () -> loginService.signUp(signUpRequest));
    }

    @Test
    void verifyEmail() {
        // email(username), token

        // given
        User user = getUserWithName("user1");
        String email = user.getUsername();

        user.generateEmailVerifyToken();
        assert !user.isEmailVerified();
        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.of(user));

        // when
        Mentee mentee = loginService.verifyEmail(email, user.getEmailVerifyToken());

        // then
        assertTrue(user.isEmailVerified());
        verify(menteeLogService).insert(user, mentee);
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
        User user = getUserWithName("user1");
        String email = user.getUsername();
        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.of(user));

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail(email, user.getEmailVerifyToken())
        );
    }

    @DisplayName("해당 사용자의 토큰이 아닌 경우")
    @Test
    void verifyEmail_wrongToken() {

        // given
        User user = getUserWithName("user1");
        String email = user.getUsername();
        user.generateEmailVerifyToken();

        when(userRepository.findUnverifiedUserByUsername(email)).thenReturn(Optional.of(user));

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail(email, "wrong_token")
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
        when(user.getPassword()).thenReturn(password);

        // when
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
        loginService.login(loginRequest);

        // then
        verify(user).login(loginLogService);
        verify(loginLogService).login(any(User.class));
        // jwt
        // 1. access-token
        verify(jwtTokenManager).createToken(any(String.class), any(Map.class));
        // 2. refresh-token
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(any(String.class));
        verify(jwtTokenManager).getJwtTokens(any(String.class), any(String.class));
    }

    @Test
    void refreshToken_when_accessToken_is_not_expired() {

        // given
        String accessToken = "access-token";
        // accessToken 만료 X
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(true);

        // when
        JwtTokenManager.JwtResponse response = loginService.refreshToken("Bearer access-token", "Bearer refresh-token", "ROLE_MENTEE");
        // then
        assertNull(response);
    }

    @Test
    void refreshToken_when_accessToken_is_expired() {
        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);
        // refreshToken
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));

        Map menteeClaims = mock(Map.class);
        String newAccessToken = "new-access-token";
        when(jwtTokenManager.createToken(user.getUsername(), menteeClaims)).thenReturn(newAccessToken);
        when(jwtTokenManager.verifyToken(refreshToken)).thenReturn(true);

        // when
        JwtTokenManager.JwtResponse result = loginService.refreshToken("Bearer access-token", "Bearer refresh-token", "ROLE_MENTEE");

        // then

        // access-token 확인
        verify(jwtTokenManager).verifyToken(accessToken);
        verify(userRepository).findByRefreshToken(refreshToken);
        // access-token 생성
        verify(jwtTokenManager).createToken("username", menteeClaims);

        // refresh-token 확인
        verify(jwtTokenManager).verifyToken(refreshToken);
        verify(jwtTokenManager).getJwtTokens(any(String.class), any(String.class));

        assertNotNull(result);
        assertEquals(refreshToken, result.getRefreshToken());
        assertNotEquals(newAccessToken, result.getAccessToken());
    }

    @Test
    void refreshToken_when_refreshToken_is_not_in_database() {

        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);
        // refreshToken
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> loginService.refreshToken("Bearer access-token", "Bearer refresh-token", "ROLE_MENTEE"));
    }

    @DisplayName("refreshToken도 만료된 경우")
    @Test
    void refreshToken_when_tokens_are_expired() {

        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        // accessToken 만료
        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(false);
        // refreshToken
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));

        Map menteeClaims = mock(Map.class);
        String newAccessToken = "new-access-token";
        when(jwtTokenManager.createToken(user.getUsername(), menteeClaims)).thenReturn(newAccessToken);
        when(jwtTokenManager.verifyToken(refreshToken)).thenReturn(true);
        String newRefreshToken = "new-refresh-token";
        when(jwtTokenManager.createRefreshToken()).thenReturn(newRefreshToken);
        // when
        JwtTokenManager.JwtResponse result
                = loginService.refreshToken("Bearer access-token", "Bearer refresh-token", "ROLE_MENTEE");

        // then

        // access-token 확인
        verify(jwtTokenManager).verifyToken(accessToken);
        verify(userRepository).findByRefreshToken(refreshToken);
        // access-token 생성
        verify(jwtTokenManager).createToken("username", menteeClaims);

        // refresh-token 확인
        verify(jwtTokenManager).verifyToken(refreshToken);
        verify(jwtTokenManager).getJwtTokens(any(String.class), any(String.class));
        // refresh-token 생성
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(newRefreshToken);

        assertNotNull(result);
        assertEquals(newRefreshToken, result.getRefreshToken());
        assertNotEquals(newAccessToken, result.getAccessToken());
    }

    @Test
    void findPassword() {
        // 랜덤 비밀번호 생성 후 메일로 전송
        // username

        // given
        String username = "user@email.com";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(user.findPassword(bCryptPasswordEncoder, userLogService)).thenReturn("_randomPassword");

        // when
        loginService.findPassword(username);

        // then
        // 1. 랜덤 비밀번호 생성 - passwordEncoder
        verify(bCryptPasswordEncoder, atLeastOnce()).encode(anyString());
        // 2. user에 set
        verify(user).updatePassword("_randomPassword", userLogService);
        // 3. 메일로 전송
        verify(emailService, atLeastOnce()).send(any(EmailMessage.class));
    }

    @Test
    void change_type_when_role_is_mentee() {

        // given
        String username = "user@email.com";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Map menteeClaims = any(Map.class);
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        when(jwtTokenManager.createToken(username, menteeClaims)).thenReturn(newAccessToken);
        when(jwtTokenManager.createRefreshToken()).thenReturn(newRefreshToken);

        // when
        JwtTokenManager.JwtResponse result = loginService.changeType(username, "ROLE_MENTEE");

        // then
        verify(jwtTokenManager).createToken(username, menteeClaims);
        // TODO - CHECK : refreshToken을 다시 발급받아야 하는가?
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(newRefreshToken);
        verify(jwtTokenManager).getJwtTokens(newAccessToken, newRefreshToken);

        assertNotNull(result);
        assertEquals(newAccessToken, result.getAccessToken());
        assertEquals(newRefreshToken, result.getRefreshToken());
    }

    @Test
    void change_type_when_role_is_mentee_but_cannot_change_to_mentor() {

        // given
        String username = "user@email.com";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getRole()).thenReturn(RoleType.MENTEE);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Map menteeClaims = any(Map.class);
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        when(jwtTokenManager.createToken(username, menteeClaims)).thenReturn(newAccessToken);
        when(jwtTokenManager.createRefreshToken()).thenReturn(newRefreshToken);

        // when
        assertThrows(UnauthorizedException.class,
                () -> loginService.changeType(username, "ROLE_MENTEE"));
    }

    @Test
    void change_type_when_role_is_mentor() {

        // given
        String username = "user@email.com";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Map mentorClaims = any(Map.class);
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        when(jwtTokenManager.createToken(username, mentorClaims)).thenReturn(newAccessToken);
        when(jwtTokenManager.createRefreshToken()).thenReturn(newRefreshToken);

        // when
        JwtTokenManager.JwtResponse result = loginService.changeType(username, "ROLE_MENTOR");

        // then
        verify(jwtTokenManager).createToken(username, mentorClaims);
        // TODO - CHECK : refreshToken을 다시 발급받아야 하는가?
        verify(jwtTokenManager).createRefreshToken();
        verify(user).updateRefreshToken(newRefreshToken);
        verify(jwtTokenManager).getJwtTokens(newAccessToken, newRefreshToken);

        assertNotNull(result);
        assertEquals(newAccessToken, result.getAccessToken());
        assertEquals(newRefreshToken, result.getRefreshToken());
    }

}