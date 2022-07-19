package com.project.mentoridge.modules.inquiry.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.service.InquiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.init.TestDataBuilder.getInquiryCreateRequestWithInquiryType;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InquiryController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class InquiryControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/users/my-inquiry";

    @MockBean
    InquiryService inquiryService;


    @Test
    void new_inquiry() throws Exception {

        // given
        // when
        // then
        InquiryCreateRequest inquiryCreateRequest = getInquiryCreateRequestWithInquiryType(InquiryType.MENTEE);
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(inquiryService).createInquiry(eq(user), eq(inquiryCreateRequest));
    }

    @Test
    void new_inquiry_with_no_inputs() throws Exception {

        // given
        // when
        // then
        InquiryCreateRequest inquiryCreateRequest = InquiryCreateRequest.builder()
                .type(null)
                .title(null)
                .content(null)
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void new_inquiry_with_no_auth() throws Exception {

        // given
        // when
        // then
        InquiryCreateRequest inquiryCreateRequest = getInquiryCreateRequestWithInquiryType(InquiryType.MENTEE);
        mockMvc.perform(post(BASE_URL)
                        .content(objectMapper.writeValueAsString(inquiryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

//    @DisplayName("RabbitMQ 테스트")
//    @Test
//    void test() throws Exception {
//
//        // given
//        doReturn(Mockito.mock(Inquiry.class))
//                .when(inquiryService).test(any(InquiryCreateRequest.class));
//        // when
//        // then
//        InquiryCreateRequest inquiryCreateRequest = getInquiryCreateRequestWithInquiryType(InquiryType.MENTEE);
//        mockMvc.perform(post(BASE_URL + "/test-producer")
//                .content(objectMapper.writeValueAsString(inquiryCreateRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}