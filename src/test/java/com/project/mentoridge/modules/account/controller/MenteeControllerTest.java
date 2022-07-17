package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.service.MenteeService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenteeControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees";

    @Mock
    MenteeService menteeService;
    @InjectMocks
    MenteeController menteeController;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
                .standaloneSetup(menteeController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
        assertNotNull(mockMvc);
    }

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