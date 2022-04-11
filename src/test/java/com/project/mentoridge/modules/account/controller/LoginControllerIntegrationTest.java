package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.config.security.oauth.provider.google.GoogleInfo;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.getLoginRequestWithUsernameAndPassword;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class LoginControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PrincipalDetailsService principalDetailsService;
    @Autowired
    JwtTokenManager jwtTokenManager;


    // TODO - OAuth 테스트
    // @Test
//    void oauth() {
//    }

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
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNull(user);

        User createdUser = userRepository.findAllByUsername(USERNAME);
        assertAll(
                () -> assertEquals(RoleType.MENTEE, createdUser.getRole()),
                () -> assertEquals(signUpRequest.getGender(), createdUser.getGender().toString()),
                () -> assertFalse(createdUser.isEmailVerified()),
                () -> assertNull(createdUser.getEmailVerifiedAt())
        );

        Mentee mentee = menteeRepository.findByUser(createdUser);
        assertNull(mentee);
    }

    @Test
    @DisplayName("일반 회원가입 - Invalid Input")
    public void signUp_withInvalidInput() throws Exception {

        // Given
        // When
        signUpRequest.setPasswordConfirm("password_");

        // Then
        mockMvc.perform(post("/api/sign-up")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
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
                .andExpect(status().isOk());

        // Then
        User verifiedUser = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertNotNull(verifiedUser),
                () -> assertTrue(verifiedUser.isEmailVerified()),
                () -> assertNotNull(verifiedUser.getEmailVerifiedAt())
        );

        Mentee mentee = menteeRepository.findByUser(verifiedUser);
        assertNotNull(mentee);

    }

    @DisplayName("일반 로그인 후 jwtToken 확인")
    @Test
    void login() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        MockHttpServletResponse response = mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(header().exists("Authorization"))
                .andReturn().getResponse();

        // Then
        assertTrue(response.getContentAsString().startsWith("Bearer"));
    }

    // java.lang.AssertionError: No value at JSON path "$.code"
    @Test
    void 로그인_실패() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        // loginRequest가 static 변수라서 위 테스트와 같이 실행하면 위 테스트 실패
        // loginRequest.setPassword("password_");
        LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(USERNAME, "password_");
        mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHENTICATED.getCode()));
    }

    @Test
    void findPassword() {
    }

    // TODO
    @DisplayName("OAuth 회원가입 후 상세정보 저장")
    @Test
    void signUpOAuthDetail() throws Exception {

        // Given
        Map<String, Object> attributes = new HashMap<>();
        // loginService.signUpOAuth(new GoogleInfo(attributes));
        fail();

        PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(USERNAME);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                .content(objectMapper.writeValueAsString(signUpOAuthDetailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(OAuthType.GOOGLE, user.getProvider()),
                () -> assertEquals(RoleType.MENTEE, user.getRole()),
                () -> assertEquals(GenderType.FEMALE, user.getGender()),
                () -> assertEquals(NICKNAME, user.getNickname())
        );

        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);
    }

    @DisplayName("회원 정보 추가 입력 - OAuth 가입이 아닌 경우")
    @WithAccount(NAME)
    @Test
    void signUpOAuthDetail_notOAuthUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                .content(objectMapper.writeValueAsString(signUpOAuthDetailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

    }
}