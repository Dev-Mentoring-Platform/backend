package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.userUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MenteeRepository menteeRepository;

    private User user1;
    private Mentee mentee1;
    private Mentor mentor1;
    private User user2;
    private Mentee mentee2;
    private User user3;
    private User user4;

    @BeforeEach
    void BeforeEach() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        // user1 - mentor
        user1 = saveMentorUser(loginService, mentorService);
        mentee1 = menteeRepository.findByUser(user1);
        mentor1 = mentorRepository.findByUser(user1);

        // user2 - mentee
        user2 = saveMenteeUser(loginService);
        mentee2 = menteeRepository.findByUser(user2);

        // user3 - not email-verified
        SignUpRequest signUpRequest3 = SignUpRequest.builder()
                .username("user3@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("userName3")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("userNickname3")
                .build();
        user3 = loginService.signUp(signUpRequest3);

        // user4 - deleted
        SignUpRequest signUpRequest4 = SignUpRequest.builder()
                .username("user4@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("userName4")
                .gender(GenderType.MALE)
                .birthYear("1996")
                .phoneNumber("01044445555")
                .nickname("userNickname4")
                .build();
        user4 = loginService.signUp(signUpRequest4);
        user4.generateEmailVerifyToken();
        loginService.verifyEmail(user4.getUsername(), user4.getEmailVerifyToken());

        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .password("password")
                .reasonId(1)
                .build();
        userService.deleteUser(user4, userQuitRequest);
    }

    @Test
    void get_paged_UserResponses() {

        // Given
        // When
        Page<UserResponse> responses = userService.getUserResponses(1);
        // Then
        assertThat(responses).hasSize(4);
        for (UserResponse response : responses) {

            if (response.getUserId().equals(user1.getId())) {

                assertThat(response.getUserId()).isEqualTo(user1.getId());
                assertThat(response.getUsername()).isEqualTo(user1.getUsername());
                assertThat(response.getRole()).isEqualTo(user1.getRole());
                assertThat(response.getName()).isEqualTo(user1.getName());
                assertThat(response.getGender()).isEqualTo(user1.getGender());
                assertThat(response.getBirthYear()).isEqualTo(user1.getBirthYear());
                assertThat(response.getPhoneNumber()).isEqualTo(user1.getPhoneNumber());
                assertThat(response.getNickname()).isEqualTo(user1.getNickname());
                assertThat(response.getImage()).isEqualTo(user1.getImage());
                assertThat(response.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user1.getZone()));

            } else if (response.getUserId().equals(user2.getId())) {

                assertThat(response.getUserId()).isEqualTo(user2.getId());
                assertThat(response.getUsername()).isEqualTo(user2.getUsername());
                assertThat(response.getRole()).isEqualTo(user2.getRole());
                assertThat(response.getName()).isEqualTo(user2.getName());
                assertThat(response.getGender()).isEqualTo(user2.getGender());
                assertThat(response.getBirthYear()).isEqualTo(user2.getBirthYear());
                assertThat(response.getPhoneNumber()).isEqualTo(user2.getPhoneNumber());
                assertThat(response.getNickname()).isEqualTo(user2.getNickname());
                assertThat(response.getImage()).isEqualTo(user2.getImage());
                assertThat(response.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user2.getZone()));

            } else if (response.getUserId().equals(user3.getId())) {

                assertThat(response.getUserId()).isEqualTo(user3.getId());
                assertThat(response.getUsername()).isEqualTo(user3.getUsername());
                assertThat(response.getRole()).isEqualTo(user3.getRole());
                assertThat(response.getName()).isEqualTo(user3.getName());
                assertThat(response.getGender()).isEqualTo(user3.getGender());
                assertThat(response.getBirthYear()).isEqualTo(user3.getBirthYear());
                assertThat(response.getPhoneNumber()).isEqualTo(user3.getPhoneNumber());
                assertThat(response.getNickname()).isEqualTo(user3.getNickname());
                assertThat(response.getImage()).isEqualTo(user3.getImage());
                assertThat(response.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user3.getZone()));

            } else if (response.getUserId().equals(user4.getId())) {

                assertThat(response.getUserId()).isEqualTo(user4.getId());
                assertThat(response.getUsername()).isEqualTo(user4.getUsername());
                assertThat(response.getRole()).isEqualTo(user4.getRole());
                assertThat(response.getName()).isEqualTo(user4.getName());
                assertThat(response.getGender()).isEqualTo(user4.getGender());
                assertThat(response.getBirthYear()).isEqualTo(user4.getBirthYear());
                assertThat(response.getPhoneNumber()).isEqualTo(user4.getPhoneNumber());
                assertThat(response.getNickname()).isEqualTo(user4.getNickname());
                assertThat(response.getImage()).isEqualTo(user4.getImage());
                assertThat(response.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user4.getZone()));

            } else {
                fail();
            }

        }
    }

    @Test
    void get_UserResponse() {

        // Given
        // When
        UserResponse response1 = userService.getUserResponse(user1.getId());
        UserResponse response2 = userService.getUserResponse(user2.getId());
        UserResponse response3 = userService.getUserResponse(user3.getId());
        UserResponse response4 = userService.getUserResponse(user4.getId());

        // Then
        assertThat(response1.getUserId()).isEqualTo(user1.getId());
        assertThat(response1.getUsername()).isEqualTo(user1.getUsername());
        assertThat(response1.getRole()).isEqualTo(user1.getRole());
        assertThat(response1.getName()).isEqualTo(user1.getName());
        assertThat(response1.getGender()).isEqualTo(user1.getGender());
        assertThat(response1.getBirthYear()).isEqualTo(user1.getBirthYear());
        assertThat(response1.getPhoneNumber()).isEqualTo(user1.getPhoneNumber());
        assertThat(response1.getNickname()).isEqualTo(user1.getNickname());
        assertThat(response1.getImage()).isEqualTo(user1.getImage());
        assertThat(response1.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user1.getZone()));

        assertThat(response2.getUserId()).isEqualTo(user2.getId());
        assertThat(response2.getUsername()).isEqualTo(user2.getUsername());
        assertThat(response2.getRole()).isEqualTo(user2.getRole());
        assertThat(response2.getName()).isEqualTo(user2.getName());
        assertThat(response2.getGender()).isEqualTo(user2.getGender());
        assertThat(response2.getBirthYear()).isEqualTo(user2.getBirthYear());
        assertThat(response2.getPhoneNumber()).isEqualTo(user2.getPhoneNumber());
        assertThat(response2.getNickname()).isEqualTo(user2.getNickname());
        assertThat(response2.getImage()).isEqualTo(user2.getImage());
        assertThat(response2.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user2.getZone()));

        assertThat(response3.getUserId()).isEqualTo(user3.getId());
        assertThat(response3.getUsername()).isEqualTo(user3.getUsername());
        assertThat(response3.getRole()).isEqualTo(user3.getRole());
        assertThat(response3.getName()).isEqualTo(user3.getName());
        assertThat(response3.getGender()).isEqualTo(user3.getGender());
        assertThat(response3.getBirthYear()).isEqualTo(user3.getBirthYear());
        assertThat(response3.getPhoneNumber()).isEqualTo(user3.getPhoneNumber());
        assertThat(response3.getNickname()).isEqualTo(user3.getNickname());
        assertThat(response3.getImage()).isEqualTo(user3.getImage());
        assertThat(response3.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user3.getZone()));

        assertThat(response4.getUserId()).isEqualTo(user4.getId());
        assertThat(response4.getUsername()).isEqualTo(user4.getUsername());
        assertThat(response4.getRole()).isEqualTo(user4.getRole());
        assertThat(response4.getName()).isEqualTo(user4.getName());
        assertThat(response4.getGender()).isEqualTo(user4.getGender());
        assertThat(response4.getBirthYear()).isEqualTo(user4.getBirthYear());
        assertThat(response4.getPhoneNumber()).isEqualTo(user4.getPhoneNumber());
        assertThat(response4.getNickname()).isEqualTo(user4.getNickname());
        assertThat(response4.getImage()).isEqualTo(user4.getImage());
        assertThat(response4.getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(user4.getZone()));
    }

    @WithAccount("user")
    @Test
    void User_수정() {

        // Given
        User user = userRepository.findByUsername("user").orElse(null);

        // When
        userService.updateUser(user, userUpdateRequest);

        // Then
        User updatedUser = userRepository.findByUsername("user").orElse(null);
        assert updatedUser != null;
        assert user != null;
        assertAll(
                () -> assertEquals(RoleType.MENTEE, updatedUser.getRole()),
                () -> assertEquals(userUpdateRequest.getGender(), user.getGender()),
                () -> assertEquals(userUpdateRequest.getBirthYear(), user.getBirthYear()),
                () -> assertEquals(userUpdateRequest.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(userUpdateRequest.getZone(), user.getZone().toString()),
                () -> assertEquals(userUpdateRequest.getImage(), user.getImage())
        );
    }

    // @WithAccount("user")
    @Test
    void User_탈퇴() {

        // Given
        String username = user1.getUsername();
        assertEquals(RoleType.MENTOR, user1.getRole());

        // When
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password(user1.getPassword())
                .build();
        userService.deleteUser(user1, userQuitRequest);

        // Then
        User deletedUser = userRepository.findByUsername(username).orElse(null);
        assertNull(deletedUser);

        deletedUser = userRepository.findAllByUsername(username);
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedAt());

        // 기본 Role - 멘티
        assertFalse(mentorRepository.findById(mentor1.getId()).isPresent());
        assertFalse(menteeRepository.findById(mentee1.getId()).isPresent());
        assertEquals(RoleType.MENTEE, deletedUser.getRole());





    }

    @Test
    void update_userPassword() {

    }

    @Test
    void update_userImage() {

    }

    @Test
    void update_userFcmToken() {

    }
}