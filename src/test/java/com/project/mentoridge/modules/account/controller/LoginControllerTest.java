package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.SessionUser;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.OAuthLoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class LoginControllerTest extends AbstractControllerTest {

    @MockBean
    LoginService loginService;
    @MockBean
    OAuthLoginService oAuthLoginService;

    @WithMockUser(username = "user@email.com", roles = {"MENTEE"})
    @Test
    void change_type() throws Exception {

        // given
//        Map<String, String> result = new HashMap<>();
//        result.put("token", "token");
//        doReturn(result)
//                .when(loginService.changeType("user1@email.com", "ROLE_MENTEE"));

        // when
        // then
        mockMvc.perform(get("/api/change-type"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(loginService).changeType(eq("user@email.com"), eq("ROLE_MENTEE"));
    }

    @DisplayName("멘토 전환 가능여부 확인")
    @Test
    void check_role_when_role_is_mentor() throws Exception {

        // given
        PrincipalDetails principalDetails = new PrincipalDetails(User.builder()
                .role(RoleType.MENTOR)
                .build());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        mockMvc.perform(get("/api/check-role"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @DisplayName("멘토 전환 가능여부 확인 - 실패")
    @Test
    void check_role_when_role_is_mentee() throws Exception {

        // given
        PrincipalDetails principalDetails = new PrincipalDetails(User.builder()
                .role(RoleType.MENTEE)
                .build());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        mockMvc.perform(get("/api/check-role"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void get_sessionUser() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/session-user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new SessionUser(principalDetails))));
    }

    @DisplayName("회원가입")
    @Test
    void signUp() throws Exception {

        // given
        // when
        // then
        SignUpRequest request = getSignUpRequestWithNameAndNickname("user", "user");
        mockMvc.perform(post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(loginService).signUp(any(SignUpRequest.class));
    }

    @Test
    void signUp_duplicatedUsername() throws Exception {

        // given
        doThrow(new AlreadyExistException(AlreadyExistException.ID))
                .when(loginService).signUp(any(SignUpRequest.class));
        // when
        // then
        SignUpRequest request = getSignUpRequestWithNameAndNickname("user", "user");
        mockMvc.perform(post("/api/sign-up").header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_oAuthDetail() throws Exception {

        // given
        // when
        // then
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = getSignUpOAuthDetailRequestWithNickname("user");
        mockMvc.perform(post("/api/sign-up/oauth/detail").header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpOAuthDetailRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(oAuthLoginService).signUpOAuthDetail(any(User.class), eq(signUpOAuthDetailRequest));
    }

    @Test
    void signUp_oAuthDetail_invalid() throws Exception {

        // given
        // when
        // then
        SignUpOAuthDetailRequest request = SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("-")
                .nickname("nickname")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                        .contentType(MediaType.APPLICATION_JSON).header(AUTHORIZATION, accessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(oAuthLoginService);
    }

    @Test
    void checkUsername() throws Exception {

        // given
        String username = "user";
        doReturn(true)
                .when(loginService).checkUsernameDuplication(username);
        // when
        // then
        mockMvc.perform(get("/api/check-username").param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void checkUsername_noParam() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/check-username")
                        .param("username", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(loginService);
    }

    @Test
    void _checkUsername_noParam() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/check-username"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(loginService);
    }

    @Test
    void checkNickname() throws Exception {

        // given
        String nickname = "user";
        doReturn(true)
                .when(loginService).checkNicknameDuplication(nickname);
        // when
        // then
        mockMvc.perform(get("/api/check-nickname").param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void verifyEmail() throws Exception {

        // given
        String email = "user@email.com";
        String token = "token";
        // when
        // then
        mockMvc.perform(get("/api/verify-email")
                        .param("email", email)
                        .param("token", token))
                .andDo(print())
                .andExpect(status().isOk());
        verify(loginService).verifyEmail(eq(email), eq(token));
    }

    @Test
    void verifyEmail_notExistUser() throws Exception {

        // given
        String email = "user@email.com";
        String token = "token";
        doThrow(new RuntimeException("해당 계정의 미인증 사용자가 존재하지 않습니다."))
                .when(loginService).verifyEmail(email, token);
        // when
        // then
        mockMvc.perform(get("/api/verify-email")
                        .param("email", email)
                        .param("token", token))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void verifyEmail_notVerified() throws Exception {

        // given
        String email = "user@email.com";
        String token = "token";
        doThrow(new RuntimeException("인증 실패"))
                .when(loginService).verifyEmail(email, token);
        // when
        // then
        mockMvc.perform(get("/api/verify-email")
                        .param("email", email)
                        .param("token", token))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void login() throws Exception {

        // given
        LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword("user@email.com", "password");
        JwtTokenManager.JwtResponse result = mock(JwtTokenManager.JwtResponse.class);
        when(loginService.login(loginRequest)).thenReturn(result);

        // when
        // then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_ACCESS_TOKEN))
                .andExpect(header().exists(HEADER_REFRESH_TOKEN));
    }

    @Test
    void findPassword() throws Exception {

        // given
        String username = "user";
        // when
        // then
        mockMvc.perform(get("/api/find-password")
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
        verify(loginService).findPassword(eq(username));
    }

    @Test
    void refresh_token_when_no_token_is_expired() throws Exception {

        // Given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String role = RoleType.MENTOR.getType();
        when(loginService.refreshToken(accessToken, refreshToken, role)).thenReturn(null);

        // When
        // Then
        mockMvc.perform(post("/api/refresh-token")
                .header(HEADER_ACCESS_TOKEN, "Bearer " + accessToken)
                .header(HEADER_REFRESH_TOKEN, "Bearer " + refreshToken)
                .header("role", RoleType.MENTOR.getType()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HEADER_ACCESS_TOKEN))
                .andExpect(header().doesNotExist(HEADER_REFRESH_TOKEN));
    }

    @Test
    void refresh_token() throws Exception {

        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String role = RoleType.MENTOR.getType();

        JwtTokenManager.JwtResponse result = mock(JwtTokenManager.JwtResponse.class);
        when(loginService.refreshToken(accessToken, refreshToken, role)).thenReturn(result);

        // when
        // then
        mockMvc.perform(post("/api/refresh-token")
                        .header(HEADER_ACCESS_TOKEN, "Bearer " + accessToken)
                        .header(HEADER_REFRESH_TOKEN, "Bearer " + refreshToken)
                        .header("role", RoleType.MENTOR.getType()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_ACCESS_TOKEN))
                .andExpect(header().exists(HEADER_REFRESH_TOKEN));
    }

    @Test
    void refresh_token_when_refreshToken_is_not_in_database() throws Exception {

        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String role = RoleType.MENTOR.getType();
//        JwtTokenManager.JwtResponse result = mock(JwtTokenManager.JwtResponse.class);
        when(loginService.refreshToken(accessToken, refreshToken, role)).thenThrow(RuntimeException.class);

        // when
        // then
        mockMvc.perform(post("/api/refresh-token")
                .header(HEADER_ACCESS_TOKEN, "Bearer " + accessToken)
                .header(HEADER_REFRESH_TOKEN, "Bearer " + refreshToken)
                .header("role", RoleType.MENTOR.getType()))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

}