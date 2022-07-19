package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.service.MenteeService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenteeController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MenteeControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees";

    @MockBean
    MenteeService menteeService;

    @Test
    void getMentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeService).getMenteeResponses(eq(1));
    }

    @Test
    void getMentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeService).getMenteeResponse(1L);
    }

    // https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html
    @Test
    void editMentee_withoutAuth() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeUpdateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(menteeService);
        verify(menteeService, atMost(0)).updateMentee(any(User.class), any(MenteeUpdateRequest.class));
    }

    @Test
    void editMentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeService).updateMentee(any(User.class), eq(menteeUpdateRequest));
    }
}