package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class EducationControllerIntegrationTest {

    private final static String BASE_URL = "/api/educations";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    JwtTokenManager jwtTokenManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    EducationService educationService;
    @Autowired
    EducationRepository educationRepository;

    private String jwtToken;

    @BeforeEach
    void setup() {

        // token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", USERNAME);
        claims.put("role", RoleType.MENTOR.getType());
        jwtToken = TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @Test
    @WithAccount(NAME)
    void getEducation() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);

        Education education = educationRepository.findByMentor(mentor).stream().findFirst()
                .orElseThrow(Exception::new);

        // When
        // Then
        mockMvc.perform(get(BASE_URL + "{education_id}", education.getId())
                .header(HEADER, jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.educationLevel").exists())
                .andExpect(jsonPath("$.schoolName").exists())
                .andExpect(jsonPath("$.major").exists())
                .andExpect(jsonPath("$.others").exists());
    }

    @WithAccount(NAME)
    @Test
    void Education_등록() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, jwtToken)
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

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then - Invalid Input
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, jwtToken)
                .content(objectMapper.writeValueAsString(educationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
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
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", USERNAME);
        claims.put("role", RoleType.MENTEE.getType());
        String token = TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, token)
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
                .header(HEADER, jwtToken)
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
        mockMvc.perform(delete(BASE_URL + "/{educationId}", educationId)
                .header(HEADER, jwtToken))
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