package com.project.mentoridge.modules.account.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.config.security.oauth.OAuthAttributes;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.service.OAuthLoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.getLoginRequestWithUsernameAndPassword;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class LoginControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenManager jwtTokenManager;
    @Autowired
    LoginService loginService;
    @Autowired
    OAuthLoginService oAuthLoginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessTokenWithPrefix;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        // subject
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("백엔드")
                    .build());
            subjectRepository.save(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("프론트엔드")
                    .build());
        }

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    @DisplayName("멘토/멘티 전환")
    @Test
    void change_type() throws Exception {

        // Given
        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/change-type")
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // Then
        String accessTokenWithPrefix = response.getHeader(HEADER_ACCESS_TOKEN);
        String accessToken = accessTokenWithPrefix.replace("Bearer ", "");
        String refreshTokenWithPrefix = response.getHeader(HEADER_REFRESH_TOKEN);
        String refreshToken = refreshTokenWithPrefix.replace("Bearer ", "");

        assertNotNull(accessTokenWithPrefix);
        assertNotNull(refreshTokenWithPrefix);
        assertEquals(mentorUser.getUsername(), jwtTokenManager.getClaim(accessToken, "username"));
        assertEquals(RoleType.MENTEE.getType(), jwtTokenManager.getClaim(accessToken, "role"));
    }
/*  => 멘티만 확인
    @DisplayName("멘토 전환 가능여부 확인 - 멘토")
    @Test
    void check_role_mentor() throws Exception {

        // Given
        // When
        // Then
        String response = mockMvc.perform(get("/api/check-role")
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("true");
    }*/

    @DisplayName("멘토 전환 가능여부 확인 - 멘티")
    @Test
    void check_role_mentee() throws Exception {

        // Given
        // When
        // Then
        String response = mockMvc.perform(get("/api/check-role")
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("false");
    }

    @DisplayName("세션 조회 - 멘토 / 멘토")
    @Test
    void get_sessionUser_mentorUser_mentorMode() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/session-user")
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())))
                .andExpect(jsonPath("$.loginType").value(RoleType.MENTOR.getType()));
    }

    @DisplayName("세션 조회 - 멘토 / 멘티")
    @Test
    void get_sessionUser_mentorUser_menteeMode() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/session-user")
                .header(AUTHORIZATION, getAccessToken(mentorUser.getUsername(), RoleType.MENTEE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())))
                .andExpect(jsonPath("$.loginType").value(RoleType.MENTEE.getType()));
    }

    @DisplayName("세션 조회 - 멘티")
    @Test
    void get_sessionUser_menteeUser_menteeMode() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/session-user")
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())))
                .andExpect(jsonPath("$.loginType").value(RoleType.MENTEE.getType()));
    }

    @Test
    void signUp() throws Exception {

        // Given
        // When
        mockMvc.perform(post("/api/sign-up")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertFalse(userRepository.findByUsername(signUpRequest.getUsername()).isPresent());
        User createdUser = userRepository.findAllByUsername(USERNAME);
        assertAll(
                () -> assertEquals(RoleType.MENTEE, createdUser.getRole()),
                () -> assertEquals(signUpRequest.getGender(), createdUser.getGender()),
                () -> assertFalse(createdUser.isEmailVerified()),
                () -> assertNull(createdUser.getEmailVerifiedAt())
        );

        Mentee mentee = menteeRepository.findByUser(createdUser);
        assertNull(mentee);
    }

    @DisplayName("일반 회원가입 - Invalid Input")
    @Test
    public void signUp_withInvalidInput() throws Exception {

        // Given
        // When
        signUpRequest.setPasswordConfirm("password_");

        // Then
        mockMvc.perform(post("/api/sign-up")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("OAuth 회원가입 후 상세정보 저장")
    @Test
    void signUpOAuthDetail() throws Exception {

        // Given
        String registrationId = "Naver";
        String nameAttributeKey = "id";

        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        response.put("name", "user");
        response.put("email", "user@email.com");
        response.put("picture", null);
        response.put(nameAttributeKey, "providerId");
        attributes.put("response", response);
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        oAuthLoginService.save(oAuthAttributes);

        // When
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear("19990101")
                .phoneNumber("01012345678")
                .nickname("new_nickname")
                .zone("서울특별시 강남구 삼성동")
                .image("new_image")
                .build();
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                    .header(AUTHORIZATION, getAccessToken("user@email.com", RoleType.MENTEE))
                    .content(objectMapper.writeValueAsString(signUpOAuthDetailRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User user = userRepository.findByUsername("user@email.com").orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(user.getUsername()).isEqualTo(oAuthAttributes.getEmail()),
                () -> assertThat(user.getName()).isEqualTo(oAuthAttributes.getName()),
                () -> assertThat(user.getGender()).isEqualTo(signUpOAuthDetailRequest.getGender()),
                () -> assertThat(user.getBirthYear()).isEqualTo(signUpOAuthDetailRequest.getBirthYear()),
                () -> assertThat(user.getPhoneNumber()).isEqualTo(signUpOAuthDetailRequest.getPhoneNumber()),
                () -> assertThat(user.getNickname()).startsWith(signUpOAuthDetailRequest.getNickname()),
                () -> assertThat(user.getZone()).isEqualTo(signUpOAuthDetailRequest.getZone()),
                () -> assertThat(user.getImage()).isEqualTo(signUpOAuthDetailRequest.getImage()),
                () -> assertThat(user.getRole()).isEqualTo(RoleType.MENTEE),
                () -> assertThat(user.getProvider()).isEqualTo(OAuthType.NAVER),
                () -> assertThat(user.getProviderId()).isEqualTo("providerId")
        );
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);
    }

    @DisplayName("회원 정보 추가 입력 - OAuth 가입이 아닌 경우")
    @Test
    void signUpOAuthDetail_notOAuthUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                            .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                            .content(objectMapper.writeValueAsString(signUpOAuthDetailRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isInternalServerError());
    }

    @Test
    void verifyEmail() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);

        // When
        mockMvc.perform(get("/api/verify-email")
                    .param("email", user.getUsername())
                    .param("token", user.getEmailVerifyToken()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        // Then
        User verifiedUser = userRepository.findByUsername(user.getUsername()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertTrue(verifiedUser.isEmailVerified()),
                () -> assertNotNull(verifiedUser.getEmailVerifiedAt())
        );
        assertNotNull(menteeRepository.findByUser(verifiedUser));
    }

    @Test
    void find_password() throws Exception {

        // Given
        String password = menteeUser.getPassword();
        // When
        mockMvc.perform(get("/api/find-password")
                        .param("username", menteeUser.getUsername()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findByUsername(menteeUser.getUsername()).orElseThrow(RuntimeException::new);
        assertThat(updated.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("일반 로그인 후 accessToken 확인")
    @Test
    void login() throws Exception {

        // Given
        // When
        LoginRequest loginRequest = LoginRequest.builder()
                .username(menteeUser.getUsername())
                .password("password")
                .build();
        MockHttpServletResponse response = mockMvc.perform(post("/api/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_ACCESS_TOKEN))
                .andExpect(header().exists(HEADER_REFRESH_TOKEN))
                .andReturn().getResponse();

        // Then
        String accessTokenWithPrefix = response.getHeader(HEADER_ACCESS_TOKEN);
        assertTrue(accessTokenWithPrefix != null && accessTokenWithPrefix.startsWith("Bearer"));

        String accessToken = accessTokenWithPrefix.replace("Bearer ", "");
        assertEquals(menteeUser.getUsername(), jwtTokenManager.getClaim(accessToken, "username"));
        assertEquals(RoleType.MENTEE.getType(), jwtTokenManager.getClaim(accessToken, "role"));

        String refreshTokenWithPrefix = response.getHeader(HEADER_REFRESH_TOKEN);
        assertTrue(refreshTokenWithPrefix != null && refreshTokenWithPrefix.startsWith("Bearer"));
    }

    // java.lang.AssertionError: No value at JSON path "$.code"
    @Test
    void 로그인_실패() throws Exception {

        // Given
        // When
        // Then
        // loginRequest가 static 변수라서 위 테스트와 같이 실행하면 위 테스트 실패
        // loginRequest.setPassword("password_");
        LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(USERNAME, "password_");
        mockMvc.perform(post("/api/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHENTICATED.getCode()));
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

    private DecodedJWT getDecodedToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }
        return JWT.require(Algorithm.HMAC256("test")).build().verify(token);
    }

    @Test
    void refresh_token_when_no_token_is_expired() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .username(menteeUser.getUsername())
                .password("password")
                .build();
        JwtResponse tokens = loginService.login(loginRequest);

        // When
        // Then
        mockMvc.perform(post("/api/refresh-token")
                .header(HEADER_ACCESS_TOKEN, tokens.getAccessToken())
                .header(HEADER_REFRESH_TOKEN, tokens.getRefreshToken())
                .header("role", "ROLE_MENTEE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HEADER_ACCESS_TOKEN))
                .andExpect(header().doesNotExist(HEADER_REFRESH_TOKEN));
    }

    @Test
    void refresh_token_when_accessToken_is_expired() throws Exception {

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
        // Then
        MockHttpServletResponse response = mockMvc.perform(post("/api/refresh-token")
                                                            .header(HEADER_ACCESS_TOKEN, expiredAccessTokenWithPrefix)
                                                            .header(HEADER_REFRESH_TOKEN, refreshTokenWithPrefix)
                                                            .header("role", "ROLE_MENTEE"))
                                                            .andDo(print())
                                            .andExpect(status().isOk())
                                            .andExpect(header().exists(HEADER_ACCESS_TOKEN))
                                            .andExpect(header().exists(HEADER_REFRESH_TOKEN))
                                            .andReturn().getResponse();
        String newAccessTokenWithPrefix = response.getHeader(HEADER_ACCESS_TOKEN);
        String newRefreshTokenWithPrefix = response.getHeader(HEADER_REFRESH_TOKEN);
        assertThat(newAccessTokenWithPrefix).isNotEqualTo(expiredAccessTokenWithPrefix);
        assertThat(newRefreshTokenWithPrefix).isEqualTo(refreshTokenWithPrefix);
    }

    @Test
    void refresh_token_when_refreshToken_is_also_expired() throws Exception {

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
        // Then
        MockHttpServletResponse response = mockMvc.perform(post("/api/refresh-token")
                .header(HEADER_ACCESS_TOKEN, expiredAccessTokenWithPrefix)
                .header(HEADER_REFRESH_TOKEN, expiredRefreshTokenWithPrefix)
                .header("role", "ROLE_MENTEE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_ACCESS_TOKEN))
                .andExpect(header().exists(HEADER_REFRESH_TOKEN))
                .andReturn().getResponse();

        String newAccessTokenWithPrefix = response.getHeader(HEADER_ACCESS_TOKEN);
        String newRefreshTokenWithPrefix = response.getHeader(HEADER_REFRESH_TOKEN);
        assertThat(newAccessTokenWithPrefix).isNotEqualTo(expiredAccessTokenWithPrefix);
        assertThat(newRefreshTokenWithPrefix).isNotEqualTo(expiredRefreshTokenWithPrefix);

        User updated = userRepository.findById(menteeUser.getId()).orElseThrow(RuntimeException::new);
        assertThat(TOKEN_PREFIX + updated.getRefreshToken()).isEqualTo(newRefreshTokenWithPrefix);
    }

    @Test
    void refresh_token_when_refreshToken_is_also_expired_but_not_in_database() throws Exception {

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
        mockMvc.perform(post("/api/refresh-token")
                .header(HEADER_ACCESS_TOKEN, expiredAccessTokenWithPrefix)
                .header(HEADER_REFRESH_TOKEN, expiredRefreshTokenWithPrefix)
                .header("role", "ROLE_MENTEE"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("아이디 중복체크")
    @Test
    void check_username() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get("/api/check-username")
                        .param("username", menteeUser.getUsername()))
                .andDo(print())
                .andExpect(content().string("true"));
    }
    
    @DisplayName("닉네임 중복체크")
    @Test
    void check_nickname() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get("/api/check-nickname")
                        .param("nickname", menteeUser.getNickname()))
                .andDo(print())
                .andExpect(content().string("true"));
    }

}