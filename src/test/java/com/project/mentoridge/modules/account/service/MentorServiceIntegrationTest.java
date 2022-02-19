package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
class MentorServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @Test
    void Mentor_등록() {

        // Given
        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorRepository.findByUser(user);
        assertNotNull(mentor);
        assertEquals(RoleType.MENTOR, user.getRole());
    }

    @WithAccount(NAME)
    @Test
    void Mentor_수정() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mentorService.updateMentor(user, mentorUpdateRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTOR, user.getRole());

        Mentor mentor = mentorRepository.findByUser(user);
        assertNotNull(mentor);
    }

    @WithAccount(NAME)
    @Test
    void Mentor_탈퇴() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mentorService.deleteMentor(user);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTEE, user.getRole());

        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertNull(mentor);
    }

}