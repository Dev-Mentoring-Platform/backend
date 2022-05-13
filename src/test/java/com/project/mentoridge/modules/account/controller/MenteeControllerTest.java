package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.interceptor.AuthInterceptor;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    void getMentee() throws Exception {

        // given
        Mentee mentee = Mentee.builder()
                .user(mock(User.class))
                .build();
        MenteeResponse response = new MenteeResponse(mentee);
        doReturn(response)
                .when(menteeService).getMenteeResponse(1L);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user").hasJsonPath())
                .andExpect(jsonPath("$.user.userId").exists())
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.role").exists())
                .andExpect(jsonPath("$.user.name").exists())
                .andExpect(jsonPath("$.user.gender").exists())
                .andExpect(jsonPath("$.user.birthYear").exists())
                .andExpect(jsonPath("$.user.phoneNumber").exists())
                .andExpect(jsonPath("$.user.nickname").exists())
                .andExpect(jsonPath("$.user.image").exists())
                .andExpect(jsonPath("$.user.zone").exists())
                .andExpect(jsonPath("$.subjects").exists());
    }

    @Test
    void getMentees() throws Exception {

        // given
        Mentee mentee = Mentee.builder()
                .user(mock(User.class))
                .build();
        Page<MenteeResponse> response = new PageImpl<>(Arrays.asList(new MenteeResponse(mentee)), Pageable.ofSize(20), 1);
        doReturn(response)
                .when(menteeService).getMenteeResponses(anyInt());

        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..user").exists())
                .andExpect(jsonPath("$..user").hasJsonPath())
                .andExpect(jsonPath("$..user.userId").exists())
                .andExpect(jsonPath("$..user.username").exists())
                .andExpect(jsonPath("$..user.role").exists())
                .andExpect(jsonPath("$..user.name").exists())
                .andExpect(jsonPath("$..user.gender").exists())
                .andExpect(jsonPath("$..user.birthYear").exists())
                .andExpect(jsonPath("$..user.phoneNumber").exists())
                .andExpect(jsonPath("$..user.nickname").exists())
                .andExpect(jsonPath("$..user.image").exists())
                .andExpect(jsonPath("$..user.zone").exists())
                .andExpect(jsonPath("$..subjects").exists());
    }

    // https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html
    @Test
    void editMentee_withoutAuth() throws Exception {

        // given
        doNothing()
                .when(menteeService).updateMentee(any(User.class), any(MenteeUpdateRequest.class));
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mock(MenteeUpdateRequest.class))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editMentee() throws Exception {

        // given
        doNothing()
                .when(menteeService).updateMentee(any(User.class), any(MenteeUpdateRequest.class));
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}