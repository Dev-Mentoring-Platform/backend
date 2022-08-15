package com.project.mentoridge.modules.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.modules.base.TestDataBuilder.getLoginRequestWithUsernameAndPassword;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class LoginServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    JwtTokenManager jwtTokenManager;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;

    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MentorService mentorService;

    private User menteeUser;
    private Mentee mentee;
    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
    }

    @Test
    void check_username_existed() {

        // Given
        // When
        // Then
        boolean result = loginService.checkUsernameDuplication(menteeUser.getUsername());
        assertTrue(result);
    }

    @Test
    void check_username_not_existed() {

        // Given
        // When
        // Then
        boolean result = loginService.checkUsernameDuplication("new@email.com");
        assertFalse(result);
    }

    @Test
    void check_nickname_existed() {

        // Given
        // When
        // Then
        boolean result = loginService.checkNicknameDuplication(menteeUser.getNickname());
        assertTrue(result);
    }

    @Test
    void check_nickname_not_existed() {

        // Given
        // When
        // Then
        boolean result = loginService.checkNicknameDuplication("new");
        assertFalse(result);
    }

    @Test
    void 회원가입() {

        // Given
        // When
        User user = loginService.signUp(signUpRequest);

        // Then
        assertFalse(userRepository.findByUsername(user.getUsername()).isPresent());
        User unverifiedUser = userRepository.findAllByUsername(user.getUsername());
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

    @DisplayName("회원가입 - AlreadyExistException 발생")
    @Test
    void signUpWithExistingUsername() {

        // Given
        User user = loginService.signUp(signUpRequest);
        assertNotNull(userRepository.findAllByUsername(user.getUsername()));

        // When
        assertThrows(AlreadyExistException.class, () -> {
            loginService.signUp(signUpRequest);
        });
    }

    @Test
    void verifyEmail() {

        // Given
        User user = loginService.signUp(signUpRequest);
        assertFalse(user.isEmailVerified());
        assertFalse(userRepository.findByUsername(user.getUsername()).isPresent());
        assertNotNull(userRepository.findAllByUsername(user.getUsername()));

        // When
        Mentee mentee = loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // Then
        User verified = userRepository.findByUsername(user.getUsername()).orElseThrow(RuntimeException::new);
        assertTrue(verified.isEmailVerified());
        // mentee
        assertNotNull(mentee);
        assertEquals(verified, mentee.getUser());
    }

    @Test
    void verifyEmail_with_wrongToken() {

        // Given
        User user = loginService.signUp(signUpRequest);
        assertFalse(user.isEmailVerified());
        assertFalse(userRepository.findByUsername(user.getUsername()).isPresent());
        assertNotNull(userRepository.findAllByUsername(user.getUsername()));

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> loginService.verifyEmail(user.getUsername(), "wrong_token"));
    }

    @Test
    void login() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        JwtTokenManager.JwtResponse result = loginService.login(loginRequest);

        // Then
        String accessToken = result.get_accessToken();
        assertEquals(user.getUsername(), jwtTokenManager.getClaim(accessToken, "username"));
    }

    @DisplayName("이메일 미인증 사용자")
    @Test
    void login_by_unverifiedUser() {

        // Given
        loginService.signUp(signUpRequest);

        // When
        // Then
        assertThrows(DisabledException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void login_with_wrongPassword() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(user.getUsername(), "password_");
        assertThrows(BadCredentialsException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void find_password() {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());
        String password = user.getPassword();

        // When
        loginService.findPassword(user.getUsername());
        // Then
        assertNotEquals(password, user.getPassword());
    }

    private String createAccessToken(String subject, Map<String, Object> claims, boolean expired) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);
        Timestamp expiredAt = null;
        if (expired) {
            expiredAt = Timestamp.valueOf(now.minusSeconds(86400));
        } else {
            expiredAt = Timestamp.valueOf(now.plusSeconds(86400));
        }
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .withPayload(claims)
                .sign(Algorithm.HMAC256("test"));
    }

    private String createRefreshToken(boolean expired) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);

        Timestamp expiredAt = null;
        if (expired) {
            expiredAt = Timestamp.valueOf(now.minusSeconds(86400));
        } else {
            expiredAt = Timestamp.valueOf(now.plusSeconds(86400));
        }
        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC256("test"));
    }

    // refresh_token
    @Test
    void refresh_token_when_no_token_is_expired() {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", menteeUser.getUsername());
        claims.put("role", RoleType.MENTEE.getType());
        String accessToken = createAccessToken(menteeUser.getUsername(), claims, false);
        String refreshToken = createRefreshToken(false);
        menteeUser.updateRefreshToken(refreshToken);

        // When
        // Then
        JwtTokenManager.JwtResponse response = loginService.refreshToken(TOKEN_PREFIX + accessToken, TOKEN_PREFIX + refreshToken, "ROLE_MENTEE");
        assertNull(response);
    }

    @Test
    void refresh_token_when_accessToken_is_expired() {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", menteeUser.getUsername());
        claims.put("role", RoleType.MENTEE.getType());
        String expiredAccessToken = createAccessToken(menteeUser.getUsername(), claims, true);
        String expiredAccessTokenWithPrefix = TOKEN_PREFIX + expiredAccessToken;
        String refreshToken = createRefreshToken(false);
        String refreshTokenWithPrefix = TOKEN_PREFIX + refreshToken;
        menteeUser.updateRefreshToken(refreshToken);

        // When
        JwtTokenManager.JwtResponse response = loginService.refreshToken(expiredAccessTokenWithPrefix, refreshTokenWithPrefix, "ROLE_MENTEE");
        // Then
        assertNotNull(response);
        String newAccessTokenWithPrefix = response.getAccessToken();
        String newRefreshTokenWithPrefix = response.getRefreshToken();
        assertThat(newAccessTokenWithPrefix).isNotEqualTo(expiredAccessTokenWithPrefix);
        assertThat(newRefreshTokenWithPrefix).isEqualTo(refreshTokenWithPrefix);
    }

    @Test
    void refresh_token_when_refreshToken_is_also_expired() {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", menteeUser.getUsername());
        claims.put("role", RoleType.MENTEE.getType());
        String expiredAccessToken = createAccessToken(menteeUser.getUsername(), claims, true);
        String expiredAccessTokenWithPrefix = TOKEN_PREFIX + expiredAccessToken;
        String expiredRefreshToken = createRefreshToken(true);
        String expiredRefreshTokenWithPrefix = TOKEN_PREFIX + expiredRefreshToken;
        menteeUser.updateRefreshToken(expiredRefreshToken);

        // When
        JwtTokenManager.JwtResponse response = loginService.refreshToken(expiredAccessTokenWithPrefix, expiredRefreshTokenWithPrefix, "ROLE_MENTEE");

        // Then
        String newAccessTokenWithPrefix = response.getAccessToken();
        String newRefreshTokenWithPrefix = response.getRefreshToken();
        assertThat(newAccessTokenWithPrefix).isNotEqualTo(expiredAccessTokenWithPrefix);
        assertThat(newRefreshTokenWithPrefix).isNotEqualTo(expiredRefreshTokenWithPrefix);

        User updated = userRepository.findById(menteeUser.getId()).orElseThrow(RuntimeException::new);
        assertThat(TOKEN_PREFIX + updated.getRefreshToken()).isEqualTo(newRefreshTokenWithPrefix);
    }

    @Test
    void refresh_token_when_refreshToken_is_also_expired_but_not_in_database() {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", menteeUser.getUsername());
        claims.put("role", RoleType.MENTEE.getType());
        String expiredAccessToken = createAccessToken(menteeUser.getUsername(), claims, true);
        String expiredAccessTokenWithPrefix = TOKEN_PREFIX + expiredAccessToken;
        String expiredRefreshToken = createRefreshToken(true);
        String expiredRefreshTokenWithPrefix = TOKEN_PREFIX + expiredRefreshToken;

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> loginService.refreshToken(expiredAccessTokenWithPrefix, expiredRefreshTokenWithPrefix, "ROLE_MENTEE"));
    }

    @Test
    void change_type_when_cannot_change_to_mentor() {

        // Given
        // When
        // Then
        // 멘토로 변경 불가
        assertThrows(UnauthorizedException.class,
                () -> loginService.changeType(menteeUser.getUsername(), RoleType.MENTEE.getType()));
    }

    @Test
    void change_type() {

        // Given
        // When
        // 1. 멘토로 변경
        String mentorAccessTokenWithPrefix = loginService.changeType(mentorUser.getUsername(), RoleType.MENTEE.getType()).getAccessToken();
        String mentorAccessToken = mentorAccessTokenWithPrefix.replace("Bearer ", "");
        // 2. 멘티로 변경
        String menteeAccessTokenWithPrefix = loginService.changeType(mentorUser.getUsername(), RoleType.MENTOR.getType()).getAccessToken();
        String menteeAccessToken = menteeAccessTokenWithPrefix.replace("Bearer ", "");

        // Then
        assertThat(mentorAccessToken).isNotEqualTo(menteeAccessToken);
        assertThat(jwtTokenManager.getClaim(mentorAccessToken, "role")).isEqualTo(RoleType.MENTOR.getType());
        assertThat(jwtTokenManager.getClaim(menteeAccessToken, "role")).isEqualTo(RoleType.MENTEE.getType());
    }
}