package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndZone;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static com.project.mentoridge.configuration.AbstractTest.signUpRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteeControllerIntegrationTest {

    private final String BASE_URL = "/api/mentees";

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
    MenteeRepository menteeRepository;

    private Mentee mentee1;

    @BeforeEach
    void init() {

        // mentee1
        User user1 = loginService.signUp(getSignUpRequestWithNameAndZone("user1", "서울특별시 강서구 화곡동"));
        user1.generateEmailVerifyToken();
        loginService.verifyEmail(user1.getUsername(), user1.getEmailVerifyToken());
        mentee1 = menteeRepository.save(Mentee.builder()
                .user(user1)
                .build());

        // mentee2
        User user2 = loginService.signUp(getSignUpRequestWithNameAndZone("user2", "서울특별시 광진구 중곡동"));
        user2.generateEmailVerifyToken();
        loginService.verifyEmail(user2.getUsername(), user2.getEmailVerifyToken());
        Mentee mentee2 = menteeRepository.save(Mentee.builder()
                .user(user2)
                .build());

        // mentee3
        User user3 = loginService.signUp(getSignUpRequestWithNameAndZone("user3", "서울특별시 강남구 청담동"));
        user3.generateEmailVerifyToken();
        loginService.verifyEmail(user3.getUsername(), user3.getEmailVerifyToken());
        Mentee mentee3 = menteeRepository.save(Mentee.builder()
                .user(user3)
                .build());
    }

    @Test
    void get_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..subjects").exists())
                .andExpect(jsonPath("$..user").exists())
                .andExpect(jsonPath("$..user.userId").exists())
                .andExpect(jsonPath("$..user.username").exists())
                .andExpect(jsonPath("$..user.role").exists())
                .andExpect(jsonPath("$..user.name").exists())
                .andExpect(jsonPath("$..user.gender").exists())
                .andExpect(jsonPath("$..user.birthYear").exists())
                .andExpect(jsonPath("$..user.phoneNumber").exists())
                .andExpect(jsonPath("$..user.nickname").exists())
                .andExpect(jsonPath("$..user.image").exists())
                .andExpect(jsonPath("$..user.zone").exists());
    }

    @Test
    void get_mentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", mentee1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjects").value(mentee1.getSubjects()))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userId").value(mentee1.getUser().getId()))
                .andExpect(jsonPath("$.user.username").value(mentee1.getUser().getUsername()))
                .andExpect(jsonPath("$.user.role").value(mentee1.getUser().getRole()))
                .andExpect(jsonPath("$.user.name").value(mentee1.getUser().getName()))
                .andExpect(jsonPath("$.user.gender").value(mentee1.getUser().getGender()))
                .andExpect(jsonPath("$.user.birthYear").value(mentee1.getUser().getBirthYear()))
                .andExpect(jsonPath("$.user.phoneNumber").value(mentee1.getUser().getPhoneNumber()))
                .andExpect(jsonPath("$.user.nickname").value(mentee1.getUser().getNickname()))
                .andExpect(jsonPath("$.user.image").value(mentee1.getUser().getImage()))
                .andExpect(jsonPath("$.user.zone").value(mentee1.getUser().getZone().toString()));
    }

    @WithAccount(NAME)
    @Test
    void Mentee_수정() throws Exception {

        // Given
        // token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", USERNAME);
        claims.put("role", RoleType.MENTEE.getType());
        String accessToken = TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);

        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(HEADER, accessToken)
                .content(objectMapper.writeValueAsString(menteeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee updatedMentee = menteeRepository.findByUser(user);
        assertAll(
                () -> assertEquals(2, updatedMentee.getSubjectList().size()),
                () -> assertTrue(updatedMentee.getSubjects().contains("spring"))
        );
    }

    @Test
    @DisplayName("Mentee 수정 - 인증된 사용자 X")
    public void editMentee_withoutAuthenticatedUser() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/my-info")
                .content(objectMapper.writeValueAsString(menteeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    // TODO - Mentee 삭제 시 연관 엔티티 전체 삭제
    // 멘티 탈퇴 X -> User 탈퇴로 변경
/*    @WithAccount(NAME)
    @Test
    void Mentee_탈퇴() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);

        // When
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        // 세션
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        // 유저
        User deletedUser = userRepository.findAllByUsername(USERNAME);
        assertAll(
                () -> assertTrue(deletedUser.isDeleted()),
                () -> assertNotNull(deletedUser.getDeletedAt()),
                () -> assertEquals(RoleType.MENTEE, deletedUser.getRole())
        );

        // 멘티
        assertNull(menteeRepository.findByUser(deletedUser));
        // chatroom
        // message
        // lecture - lecturePrice, lectureSubject
        // enrollment, pick, review
        // notification
        // file

    }*/
}