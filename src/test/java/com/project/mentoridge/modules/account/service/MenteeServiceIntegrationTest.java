package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.menteeUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @WithAccount(NAME)
    @Test
    void get_MenteeResponse() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        // When
        MenteeResponse response = menteeService.getMenteeResponse(mentee.getId());
        // Then
        assert user != null;
        assertAll(
                () -> assertThat(response).extracting("user").extracting("userId").isEqualTo(user.getId()),
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(user.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(user.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(user.getName()),
                () -> assertThat(response).extracting("user").extracting("gender").isEqualTo(user.getGender().name()),
                () -> assertThat(response).extracting("user").extracting("birthYear").isEqualTo(user.getBirthYear()),
                () -> assertThat(response).extracting("user").extracting("phoneNumber").isEqualTo(user.getPhoneNumber()),
                () -> assertThat(response).extracting("user").extracting("nickname").isEqualTo(user.getNickname()),
                () -> assertThat(response).extracting("user").extracting("image").isEqualTo(user.getImage()),
                () -> assertThat(response).extracting("user").extracting("zone").isEqualTo(user.getZone().toString()),
                () -> assertThat(response).extracting("subjects").isEqualTo(mentee.getSubjects())
        );
    }

    @WithAccount(NAME)
    @Test
    void update_mentee() {

        // Given
        // When
        User _user = userRepository.findByUsername(USERNAME).orElse(null);
        menteeService.updateMentee(_user, menteeUpdateRequest);

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertAll(
                () -> assertNotNull(mentee),
                () -> {
                    assert user != null;
                    assertEquals(RoleType.MENTEE, user.getRole());
                },
                () -> assertEquals(menteeUpdateRequest.getSubjects(), mentee.getSubjects())
        );
    }

    @WithAccount(NAME)
    @Test
    void quiting_mentee_not_equals_to_quiting_user() {

        // Given
        // When
        User _user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(_user);
        menteeService.deleteMentee(_user);

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(0, chatroomRepository.findByMentee(mentee).size()),
                () -> assertEquals(0, pickRepository.findByMentee(mentee).size()),
                () -> assertEquals(0, enrollmentRepository.findByMentee(mentee).size()),
                () -> assertNull(menteeRepository.findByUser(user)),
                // not deleted
                () -> {
                    assert user != null;
                    assertFalse(user.isDeleted());
                },
                () -> {
                    assert user != null;
                    assertNull(user.getDeletedAt());
                }
        );
    }
}