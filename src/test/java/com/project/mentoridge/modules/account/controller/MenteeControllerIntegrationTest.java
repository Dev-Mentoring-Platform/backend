package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static com.project.mentoridge.configuration.AbstractTest.signUpRequest;
import static org.junit.jupiter.api.Assertions.*;
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
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;

    @WithAccount(NAME)
    @Test
    void Mentee_수정() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);
        assertEquals(0, mentee.getSubjectList().size());

        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                .content(objectMapper.writeValueAsString(menteeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Mentee updatedMentee = menteeRepository.findByUser(user);
        assertAll(
                () -> assertEquals(2, updatedMentee.getSubjectList().size()),
                () -> assertTrue(updatedMentee.getSubjects().contains("spring"))
        );
    }

    @Disabled
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