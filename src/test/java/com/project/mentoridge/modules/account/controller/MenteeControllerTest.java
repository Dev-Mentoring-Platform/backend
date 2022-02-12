package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.interceptor.AuthInterceptor;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.MenteeService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ContextConfiguration
//@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class MenteeControllerTest {

    private final static String BASE_URL = "/api/mentees";

    @Mock
    MenteeService menteeService;
    @InjectMocks
    MenteeController menteeController;

//    @Autowired
//    WebApplicationContext context;

    @InjectMocks
    AuthInterceptor authInterceptor;
//    @InjectMocks
//    JwtRequestFilter jwtRequestFilter;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
                .standaloneSetup(menteeController)
//                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
        assertNotNull(mockMvc);
    }

    @Test
    void getMentees() throws Exception {

        // given
        Mentee mentee = Mentee.of(mock(User.class));
        Page<MenteeResponse> response = new PageImpl<>(Arrays.asList(new MenteeResponse(mentee)), Pageable.ofSize(20), 1);
        doReturn(response)
                .when(menteeService).getMenteeResponses(anyInt());

        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html
//    @Test
//    void editMentee_withoutAuth() throws Exception {
//
//        // given
////        doNothing()
////                .when(menteeService).updateMentee(any(User.class), any(MenteeUpdateRequest.class));
//        // when
//        // then
//        MenteeUpdateRequest request = AbstractTest.getMenteeUpdateRequest();
//        mockMvc.perform(put(BASE_URL + "/my-info").with(anonymous())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    void editMentee() throws Exception {

        // given
        doNothing()
                .when(menteeService).updateMentee(any(User.class), any(MenteeUpdateRequest.class));
        // when
        // then
        User user = User.of(
                "user@email.com",
                "password",
                "user", null, null, null, "user@email.com",
                "user", null, null, null, RoleType.MENTEE,
                null, null
        );
        PrincipalDetails principal = new PrincipalDetails(user);
        MenteeUpdateRequest request = AbstractTest.getMenteeUpdateRequest();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
        mockMvc.perform(put(BASE_URL + "/my-info").with(securityContext(securityContext))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}