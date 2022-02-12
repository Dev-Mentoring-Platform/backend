package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.AbstractTest;
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

@Disabled
@Transactional
@MockMvcTest
class EducationControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


//    @Test
//    void getEducation() {
//    }

    @WithAccount(NAME)
    @Test
    void Education_등록() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mockMvc.perform(post("/educations")
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertEquals(1, educationRepository.findByMentor(mentor).size());

        Education createdEducation = educationRepository.findByMentor(mentor).get(0);
        assertAll(
                () -> assertEquals(educationCreateRequest.getSchoolName(), createdEducation.getSchoolName()),
                () -> assertEquals(educationCreateRequest.getMajor(), createdEducation.getMajor())
        );
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
//        mockMvc.perform(post("/educations")
//                .content(objectMapper.writeValueAsString(educationCreateRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(jsonPath("$.message").value("Invalid Input"))
//                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void Education_등록_withoutAuthenticatedUser() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then
        mockMvc.perform(post("/educations")
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
        mockMvc.perform(post("/educations")
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

        mockMvc.perform(put("/educations/" + educationId)
                .content(objectMapper.writeValueAsString(educationUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertEquals(1, educationRepository.findByMentor(mentor).size());

        Education updatedEducation = educationRepository.findByMentor(mentor).get(0);
        assertAll(
                () -> assertEquals(educationUpdateRequest.getSchoolName(), updatedEducation.getSchoolName()),
                () -> assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor())
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
        mockMvc.perform(delete("/educations/" + educationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);

        List<Education> educations = educationRepository.findByMentor(mentor);
        assertEquals(0, educations.size());
        assertFalse(educationRepository.findById(educationId).isPresent());
    }
}