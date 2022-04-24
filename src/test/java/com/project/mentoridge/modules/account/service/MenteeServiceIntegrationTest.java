package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MenteeServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeService menteeService;
    @Autowired
    MenteeRepository menteeRepository;

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
    void quiting_mentee_not_equals_to_quiting_user() {

        // Given
        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        menteeService.deleteMentee(user);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNotNull(user);
        assertFalse(user.isDeleted());
        assertNull(user.getDeletedAt());

        Mentee mentee = menteeRepository.findByUser(user);
        assertNull(mentee);
    }
}