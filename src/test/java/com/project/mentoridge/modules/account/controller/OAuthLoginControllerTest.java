package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class OAuthLoginControllerTest {

    @Mock
    LoginService loginService;
    @InjectMocks
    LoginController loginController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

/*    @Test
    void oauth() throws Exception {

        // given
        // when
        // then
        String provider = "kakao";
        MvcResult result = mockMvc.perform(get("/oauth/{provider}", provider))
                .andDo(print())
                .andExpect(redirectedUrl("https://kauth.kakao.com/oauth/authorize?client_id=8dc9eea7e202a581e0449058e753beaf&redirect_uri=http://localhost:8080/oauth/kakao/callback&response_type=code"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        System.out.println(result);
    }

    @Test
    void oauth_unsupported() throws Exception {

        // given
        // when
        // then
        String provider = "facebook";
        mockMvc.perform(get("/oauth/{provider}", provider))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void oauthCallback() throws Exception {

        // given
        // when
        // then
    }

    @Test
    void oauthCallback_returnNull() throws Exception {

        // given
        // when
        // then
    }

    @Test
    void _oauth() throws Exception {

        // given
        // when
        // then
    }*/
}