package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteeEnrollmentControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenManager jwtTokenManager;

    private String getJwtToken(String username, RoleType roleType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @WithAccount(NAME)
    @DisplayName("수강 중인 강의 리스트")
    @Test
    void getEnrolledLectures() throws Exception {

        // given
        // when
        // then
//        mockMvc.perform(get(BASE_URL, 1).header(HEADER, jwtToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..subjects").exists())
//                .andExpect(jsonPath("$..user").exists())
//                .andExpect(jsonPath("$..user.userId").exists())
//                .andExpect(jsonPath("$..user.username").exists())
//                .andExpect(jsonPath("$..user.role").exists())
//                .andExpect(jsonPath("$..user.name").exists())
//                .andExpect(jsonPath("$..user.gender").exists())
//                .andExpect(jsonPath("$..user.birthYear").exists())
//                .andExpect(jsonPath("$..user.phoneNumber").exists())
//                .andExpect(jsonPath("$..user.nickname").exists())
//                .andExpect(jsonPath("$..user.image").exists())
//                .andExpect(jsonPath("$..user.zone").exists());
    }

    @DisplayName("수강 중인 강의 개별 조회")
    @Test
    void getEnrolledLecture() throws Exception {

        // given
        // when
        // then
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void getUnreviewedLecturesOfMentee() throws Exception {

        // given
        // when
        // then
    }

    @DisplayName("수강내역 조회")
    @Test
    void getEnrollment() throws Exception {

        // given
        // when
        // then
    }

    @DisplayName("리뷰 작성")
    @Test
    void newReview() throws Exception {

        // given
        // when
        // then
    }
}