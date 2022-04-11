package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class EducationControllerIntegrationTest extends AbstractTest {

    private final static String BASE_URL = "/api/educations";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


    @WithAccount(NAME)
    @Test
    void Education_등록() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertEquals(2, educationRepository.findByMentor(mentor).size());
    }

    @WithAccount(NAME)
    @Test
    void Education_등록_withInvalidInput() throws Exception {

//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        mentorService.createMentor(user, mentorSignUpRequest);
//
//        // When
//        // Then - Invalid Input
//
//        mockMvc.perform(post(BASE_URL)
//                .content(objectMapper.writeValueAsString(educationCreateRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(jsonPath("$.message").value("Invalid Input"))
//                .andExpect(jsonPath("$.code").value(400));
    }

    @Disabled
    @Test
    void Education_등록_withoutAuthenticatedUser() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @WithAccount(NAME)
    @Test
    void Education_등록_notMentor() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTEE, user.getRole());

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @WithAccount(NAME)
    @Test
    void Education_수정() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        Education education = educationService.createEducation(user, educationCreateRequest);
        Long educationId = education.getId();

        mockMvc.perform(put(BASE_URL + "/{educationId}", educationId)
                .content(objectMapper.writeValueAsString(educationUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertEquals(2, educationRepository.findByMentor(mentor).size());

        Education updatedEducation = educationRepository.findByMentor(mentor).stream()
                .filter(e -> e.getId().equals(educationId)).findFirst()
                .orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(educationUpdateRequest.getEducationLevel(), updatedEducation.getEducationLevel()),
                () -> assertEquals(educationUpdateRequest.getSchoolName(), updatedEducation.getSchoolName()),
                () -> assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor()),
                () -> assertEquals(educationUpdateRequest.getOthers(), updatedEducation.getOthers())
        );
    }

    @WithAccount(NAME)
    @Test
    void Education_삭제() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        Education education = educationService.createEducation(user, educationCreateRequest);
        Long educationId = education.getId();

        // When
        mockMvc.perform(delete(BASE_URL + "/{educationId}", educationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);

        List<Education> educations = educationRepository.findByMentor(mentor);
        assertEquals(1, educations.size());
        assertFalse(educationRepository.findById(educationId).isPresent());
    }
}