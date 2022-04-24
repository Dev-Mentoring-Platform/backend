package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.userUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class UserServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @WithAccount(NAME)
    @Test
    void User_수정() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        userService.updateUser(user, userUpdateRequest);

        // Then
        User updatedUser = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertEquals(RoleType.MENTEE, updatedUser.getRole()),
                () -> assertEquals(userUpdateRequest.getGender(), user.getGender().name()),
                () -> assertEquals(userUpdateRequest.getBirthYear(), user.getBirthYear()),
                () -> assertEquals(userUpdateRequest.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(userUpdateRequest.getZone(), user.getZone().toString()),
                () -> assertEquals(userUpdateRequest.getImage(), user.getImage())
        );
    }

    // TODO - 사용자 탈퇴 시 연관 엔티티 삭제 테스트
    // 1. User가 멘티인 경우
    // 2. User가 멘토인 경우
    @WithAccount(NAME)
    @Test
    void User_탈퇴() {

//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        assertEquals(RoleType.MENTEE, user.getRole());
//
//        // When
//        userService.deleteUser(user);
//
//        // Then
//        user = userRepository.findByUsername(USERNAME).orElse(null);
//        assertNull(user);
//
//        User deletedUser = userRepository.findAllByUsername(USERNAME);
//        assertTrue(deletedUser.isDeleted());
//        assertNotNull(deletedUser.getDeletedAt());
    }
}