package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
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
import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/mentees";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;

    private User user1;
    private Mentee mentee1;
//    private String menteeAccessToken1;

    private User user2;
    private Mentee mentee2;
//    private String menteeAccessToken2;

    private User user3;
    private Mentee mentee3;
//    private String menteeAccessToken3;

    @BeforeEach
    void init() {

        user1 = saveMenteeUser("user1", "서울특별시 강서구 화곡동", loginService);
        mentee1 = menteeRepository.findByUser(user1);

        user2 = saveMenteeUser("user2", "서울특별시 광진구 중곡동", loginService);
        mentee2 = menteeRepository.findByUser(user2);

        user3 = saveMenteeUser("user3", "서울특별시 강남구 청담동", loginService);
        mentee3 = menteeRepository.findByUser(user3);
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

    @Test
    void Mentee_수정() throws Exception {

        // Given
        // token
        String menteeAccessToken = getAccessToken("user1@email.com", RoleType.MENTEE);

        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, menteeAccessToken)
                .content(objectMapper.writeValueAsString(menteeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        // User user1 = userRepository.findByUsername("user1@email.com").orElse(null);
        Mentee updatedMentee = menteeRepository.findByUser(user1);
        assertAll(
                () -> assertEquals(menteeUpdateRequest.getSubjects().split(",").length, updatedMentee.getSubjectList().size()),
                () -> assertTrue(updatedMentee.getSubjects().contains(menteeUpdateRequest.getSubjects().split(",")[0]))
        );
    }

    @DisplayName("Mentee 수정 - 인증된 사용자 X")
    @Test
    public void editMentee_withoutAuthenticatedUser() throws Exception {

        // Given
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