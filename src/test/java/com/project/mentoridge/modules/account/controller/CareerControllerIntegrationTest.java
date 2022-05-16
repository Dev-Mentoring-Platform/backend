package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class CareerControllerIntegrationTest {

    private final static String BASE_URL = "/api/careers";

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
    CareerService careerService;
    @Autowired
    CareerRepository careerRepository;

    private String accessToken;

    @BeforeEach
    void setup() {

        // token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", USERNAME);
        claims.put("role", RoleType.MENTOR.getType());
        accessToken = TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @Test
    @WithAccount(NAME)
    void getCareer() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);
        Career career = careerRepository.findByMentor(mentor).stream().findFirst()
                        .orElseThrow(Exception::new);

        // When
        // Then
        mockMvc.perform(get(BASE_URL + "{career_id}", career.getId())
                .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").exists())
                .andExpect(jsonPath("$.companyName").exists())
                .andExpect(jsonPath("$.others").exists())
                .andExpect(jsonPath("$.license").exists());
    }

    @Test
    @WithAccount(NAME)
    void newCareer() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, accessToken)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);

        Assertions.assertEquals(2, careerRepository.findByMentor(mentor).size());
    }

    @Test
    @DisplayName("Career 등록 - Invalid Input")
    @WithAccount(NAME)
    void newCareer_withInvalidInput() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then - Invalid Input
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, accessToken)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("Career 등록 - 인증된 사용자 X")
    public void newCareer_withoutAuthenticatedUser() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @WithAccount(NAME)
    @Test
    @DisplayName("Career 등록 - 멘토가 아닌 경우")
    public void newCareer_notMentor() throws Exception {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", USERNAME);
        claims.put("role", RoleType.MENTEE.getType());
        String token = TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .header(HEADER, token)
                .content(objectMapper.writeValueAsString(careerCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @WithAccount(NAME)
    @Test
    void Career_수정() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // When
        mockMvc.perform(put(BASE_URL + "/{career_id}", careerId)
                .header(HEADER, accessToken)
                .content(objectMapper.writeValueAsString(careerUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);

        List<Career> careers = careerRepository.findByMentor(mentor);
        assertEquals(2, careers.size());

        Career updatedCareer = careers.stream()
                .filter(c -> c.getId().equals(careerId)).findFirst()
                .orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(careerUpdateRequest.getJob(), updatedCareer.getJob()),
                () -> assertEquals(careerUpdateRequest.getCompanyName(), updatedCareer.getCompanyName()),
                () -> assertEquals(careerUpdateRequest.getOthers(), updatedCareer.getOthers()),
                () -> assertEquals(careerUpdateRequest.getLicense(), updatedCareer.getLicense())
        );
    }

    @WithAccount(NAME)
    @Test
    void Career_삭제() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // When
        mockMvc.perform(delete(BASE_URL + "/{career_id}", careerId)
                .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        List<Career> careers = careerRepository.findByMentor(mentor);
        assertEquals(1, careers.size());
        assertFalse(careerRepository.findById(careerId).isPresent());
    }
}