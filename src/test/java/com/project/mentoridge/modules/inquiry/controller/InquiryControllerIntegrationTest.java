package com.project.mentoridge.modules.inquiry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class InquiryControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/users/my-inquiry";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;

    private User user;
    private String accessToken;

    @BeforeEach
    void init() {
        user = saveMenteeUser(NAME, loginService);
        accessToken = getAccessToken(user.getUsername(), RoleType.MENTEE);
    }

    @Test
    void newInquiry() throws Exception {

        // given
        // when
        // then
        InquiryCreateRequest inquiryCreateRequest = InquiryCreateRequest.builder()
                .type(InquiryType.LECTURE)
                .title("title")
                .content("content")
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void newInquiry_with_invalid_input() throws Exception {

        // given
        // when
        // then
        InquiryCreateRequest inquiryCreateRequest = InquiryCreateRequest.builder()
                .type(InquiryType.LECTURE)
                .title("")
                .content("")
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
    }
}
