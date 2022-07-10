package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.careerCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.careerUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class CareerControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/careers";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    CareerService careerService;
    @Autowired
    CareerRepository careerRepository;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Career career;

    @BeforeEach
    void init() {
        mentorUser = saveMentorUser(MENTOR_NAME, loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(MENTOR_USERNAME, RoleType.MENTOR);

        career = careerRepository.findByMentor(mentor).get(0);
    }

    @Test
    void getCareer() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{career_id}", career.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").exists())
                .andExpect(jsonPath("$.companyName").exists())
                .andExpect(jsonPath("$.others").exists())
                .andExpect(jsonPath("$.license").exists());
    }

    @Test
    void newCareer() throws Exception {

        // Given
        // When
        mockMvc.perform(post(BASE_URL)
                .header(AUTHORIZATION, mentorAccessToken)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertEquals(2, careerRepository.findByMentor(mentor).size());
    }
/*
    @DisplayName("Career 등록 - Invalid Input")
    @Test
    void newCareer_withInvalidInput() throws Exception {

        // Given
        // When
        // Then - Invalid Input
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, mentorAccessToken)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
    }*/

    @DisplayName("Career 등록 - 인증된 사용자 X")
    @Test
    public void newCareer_withoutAuthenticatedUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @DisplayName("Career 등록 - 멘토가 아닌 경우")
    @Test
    public void newCareer_as_mentee() throws Exception {

        // Given
        String accessToken = getAccessToken(MENTOR_USERNAME, RoleType.MENTEE);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .header(AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void Career_수정() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/{career_id}", career.getId())
                .header(AUTHORIZATION, mentorAccessToken)
                .content(objectMapper.writeValueAsString(careerUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Career updatedCareer = careerRepository.findById(career.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(careerUpdateRequest.getJob(), updatedCareer.getJob()),
                () -> assertEquals(careerUpdateRequest.getCompanyName(), updatedCareer.getCompanyName()),
                () -> assertEquals(careerUpdateRequest.getOthers(), updatedCareer.getOthers()),
                () -> assertEquals(careerUpdateRequest.getLicense(), updatedCareer.getLicense())
        );
    }

    @Test
    void Career_삭제() throws Exception {

        // Given
        // When
        mockMvc.perform(delete(BASE_URL + "/{career_id}", career.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertThat(careerRepository.findById(career.getId()).isPresent()).isFalse();
    }
}