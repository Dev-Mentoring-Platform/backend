package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MenteeServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @Test
    void Mentee_수정() {

        // Given
        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        menteeService.updateMentee(user, menteeUpdateRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);
        Assertions.assertEquals(RoleType.MENTEE, user.getRole());
        Assertions.assertEquals(menteeUpdateRequest.getSubjects(), mentee.getSubjects());
    }

    @WithAccount(NAME)
    @Test
    void Mentee_탈퇴() {

        // Given
        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        menteeService.deleteMentee(user);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNull(user);

        user = userRepository.findAllByUsername(USERNAME);
        assertTrue(user.isDeleted());
        assertNotNull(user.getDeletedAt());
        Mentee mentee = menteeRepository.findByUser(user);
        assertNull(mentee);
    }
}