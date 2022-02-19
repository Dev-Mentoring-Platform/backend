package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class UserControllerIntegrationTest extends AbstractTest {

    private final String BASE_URL = "/api/users";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @WithAccount(NAME)
    @Test
    void 회원정보_수정() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL)
                .content(objectMapper.writeValueAsString(userUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(userUpdateRequest.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(userUpdateRequest.getEmail(), user.getEmail()),
                () -> assertEquals(userUpdateRequest.getNickname(), user.getNickname()),
                () -> assertEquals(userUpdateRequest.getBio(), user.getBio()),
                () -> assertEquals(userUpdateRequest.getZone(), user.getZone().toString())
        );
    }

    // TODO - 회원 삭제 시 연관 엔티티 전체 삭제
    @WithAccount(NAME)
    @Test
    void 회원탈퇴() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(career -> career.getId()).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(education -> education.getId()).collect(Collectors.toList());

        // When
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        // 세션
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 유저
        User deletedUser = userRepository.findAllByUsername(USERNAME);
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedAt());
        assertEquals(RoleType.MENTEE, deletedUser.getRole());

        // 멘티
        assertNull(menteeRepository.findByUser(deletedUser));
        // 멘토
        assertNull(mentorRepository.findByUser(deletedUser));
        // career
        for (Long careerId : careerIds) {
            assertFalse(careerRepository.findById(careerId).isPresent());
        }
        // education
        for (Long educationId : educationIds) {
            assertFalse(educationRepository.findById(educationId).isPresent());
        }
        // chatroom
        // message
        // lecture - lecturePrice, lectureSubject
        // enrollment, pick, review
        // notification
        // file
    }
}