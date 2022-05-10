package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.interceptor.AuthInterceptor;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.SessionUser;
import com.project.mentoridge.config.security.jwt.JwtRequestFilter;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {


    // WebApplicationContext 주입
    @Autowired
    WebApplicationContext context;
    @Autowired
    JwtRequestFilter jwtRequestFilter;
    @Autowired
    AuthInterceptor authInterceptor;

    // @Mock
    @Autowired
    LoginService loginService;
    // @InjectMocks
    @Autowired
    LoginController loginController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
//                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
        MockMvcBuilders
                //.webAppContextSetup(context)
                //.apply(springSecurity())
                .standaloneSetup(loginController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
        assertNotNull(mockMvc);
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(loginService).isNotNull();
        assertThat(loginController).isNotNull();
    }

    @Test
    void change_type() throws Exception {

        // given
        Map<String, String> result = new HashMap<>();
        result.put("token", "token");
        doReturn(result)
                .when(loginService.changeType("user1@email.com", "ROLE_MENTEE"));

        // when
        // then
        mockMvc.perform(get("/api/change-type"))
                        //.header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("token"));
    }

    @Test
    void get_sessionUser() throws Exception {

        // given
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

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
        doReturn(Mockito.mock(User.class))
                .when(loginService).signUp(any(SignUpRequest.class));
        // when
        // then
        SignUpRequest request = getSignUpRequestWithNameAndNickname("user", "user");
        mockMvc.perform(post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void signUp_duplicatedUsername() throws Exception {

        // given
        doThrow(new AlreadyExistException(AlreadyExistException.ID))
                .when(loginService).signUp(any(SignUpRequest.class));
        // when
        // then
        SignUpRequest request = getSignUpRequestWithNameAndNickname("user", "user");
        mockMvc.perform(post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_oAuthDetail() throws Exception {
        // given
        doNothing()
                .when(loginService).signUpOAuthDetail(any(User.class), any(SignUpOAuthDetailRequest.class));
        // when
        // then
        SignUpOAuthDetailRequest request = getSignUpOAuthDetailRequestWithNickname("user");
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void signUp_oAuthDetail_invalid() throws Exception {

        // given
//        doNothing()
//                .when(loginService).signUpOAuthDetail(any(User.class), any(SignUpOAuthDetailRequest.class));
        // when
        // then
        SignUpOAuthDetailRequest request = getSignUpOAuthDetailRequestWithNickname("user");
        request.setGender("user");
        mockMvc.perform(post("/api/sign-up/oauth/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUsername() throws Exception {

        // given
        String username = "user";
        doReturn(true)
                .when(loginService).checkUsernameDuplication(username);
        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/check-username").param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"))
                .andReturn();
        System.out.println(result);
    }

    @Test
    void checkUsername_noParam() throws Exception {

        // given
        doCallRealMethod()
                .when(loginService).checkUsernameDuplication(anyString());
        // when
        // then
        mockMvc.perform(get("/api/check-username")
                        .param("username", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void _checkUsername_noParam() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/check-username"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkNickname() throws Exception {

        // given
        String nickname = "user";
        doReturn(true)
                .when(loginService).checkNicknameDuplication(nickname);
        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/check-nickname").param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"))
                .andReturn();
        System.out.println(result);
    }

    @Test
    void verifyEmail() throws Exception {

        // given
        String email = "user@email.com";
        String token = "token";
        Mentee mentee = Mockito.mock(Mentee.class);
        doReturn(mentee)
                .when(loginService).verifyEmail(email, token);
        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/verify-email")
                        .param("email", email)
                        .param("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result);
    }

    // TODO - CHECK
//    @Test
//    void verifyEmail_invalid() throws Exception {
//
//        // given
//        String email = "user";
//        String token = "token";
//        Mentee mentee = Mockito.mock(Mentee.class);
//        doReturn(mentee)
//                .when(loginService).verifyEmail(email, token);
//        // when
//        // then
//        mockMvc.perform(get("/verify-email")
//                .param("email", email)
//                .param("token", token))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }

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
        Map result = mock(Map.class);
        when(result.get("token")).thenReturn("abcd");
        when(loginService.login(loginRequest)).thenReturn(result);

        // when
        // then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
                //.andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("abcd"));
    }

    @Test
    void findPassword() throws Exception {

        // given
        String username = "user";
        doNothing()
                .when(loginService).findPassword(username);
        // when
        // then
        mockMvc.perform(get("/api/find-password")
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

}