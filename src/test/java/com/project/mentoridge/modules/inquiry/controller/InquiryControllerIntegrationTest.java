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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
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
    @Override
    protected void init() {
        super.init();

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
        // [{"codes":["NotBlank.inquiryCreateRequest.title","NotBlank.title","NotBlank.java.lang.String","NotBlank"],"arguments":[{"codes":["inquiryCreateRequest.title","title"],"arguments":null,"defaultMessage":"title","code":"title"}],"defaultMessage":"제목을 입력해주세요.","objectName":"inquiryCreateRequest","field":"title","rejectedValue":"","bindingFailure":false,"code":"NotBlank"},{"codes":["NotBlank.inquiryCreateRequest.content","NotBlank.content","NotBlank.java.lang.String","NotBlank"],"arguments":[{"codes":["inquiryCreateRequest.content","content"],"arguments":null,"defaultMessage":"content","code":"content"}],"defaultMessage":"상세 내용을 작성해주세요.","objectName":"inquiryCreateRequest","field":"content","rejectedValue":"","bindingFailure":false,"code":"NotBlank"}]
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..field", notNullValue()));
    }
}
