package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.EducationService;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class EducationControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/educations";

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
    EducationService educationService;
    @Autowired
    EducationRepository educationRepository;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    private Education education;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        mentorUser = saveMentorUser(MENTOR_NAME, loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(MENTOR_USERNAME, RoleType.MENTOR);

        education = educationRepository.findByMentor(mentor).get(0);
    }

    @Test
    void getEducation() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{education_id}", education.getId())
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.educationLevel").exists())
                .andExpect(jsonPath("$.schoolName").exists())
                .andExpect(jsonPath("$.major").exists())
                .andExpect(jsonPath("$.others").exists());
    }

    @Test
    void Education_등록() throws Exception {

        // Given
        // When
        mockMvc.perform(post(BASE_URL)
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertEquals(2, educationRepository.findByMentor(mentor).size());
    }
/*
    @Test
    void Education_등록_withInvalidInput() throws Exception {

        // Given
        // When
        // Then - Invalid Input
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, mentorAccessTokenWithPrefix)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
    }*/

    @Test
    void Education_등록_withoutAuthenticatedUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void Education_등록_as_mentee() throws Exception {

        // Given
        String accessToken = getAccessToken(MENTOR_USERNAME, RoleType.MENTEE);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .header(AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());
                //.andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void Education_수정() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/{educationId}", education.getId())
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                .content(objectMapper.writeValueAsString(educationUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Education updatedEducation = educationRepository.findById(education.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(educationUpdateRequest.getEducationLevel(), updatedEducation.getEducationLevel()),
                () -> assertEquals(educationUpdateRequest.getSchoolName(), updatedEducation.getSchoolName()),
                () -> assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor()),
                () -> assertEquals(educationUpdateRequest.getOthers(), updatedEducation.getOthers())
        );
    }

    @Test
    void Education_삭제() throws Exception {

        // Given
        // When
        mockMvc.perform(delete(BASE_URL + "/{educationId}", education.getId())
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertThat(educationRepository.findById(education.getId()).isPresent()).isFalse();
    }
}