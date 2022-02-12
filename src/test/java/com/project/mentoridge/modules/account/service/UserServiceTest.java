package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.InvalidInputException;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    MentorService mentorService;
    @Mock
    MenteeService menteeService;

    @DisplayName("Converter 테스트")
    @Test
    void getUserResponse() {

        // given
        User user = User.of(
                "user@email.com",
                "password",
                "name",
                "MALE",
                null,
                "01012345678",
                "user@email.com",
                "user",
                "bio",
                "서울특별시 광진구 중곡동",
                null,
                RoleType.MENTEE,
                null,
                null
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUserResponse(1L);
        // then
        System.out.println(response.getZone());
    }

    @Test
    void updateUser() {
        // user, userUpdateRequest

        // given
        User user = Mockito.mock(User.class);
//        User user = User.of(
//                "user@email.com",
//                "password",
//                "name",
//                "MALE",
//                null,
//                "01012345678",
//                "user@email.com",
//                "user",
//                "bio",
//                "서울특별시 광진구 중곡동",
//                null,
//                RoleType.MENTEE,
//                null,
//                null
//        );
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        UserUpdateRequest userUpdateRequest = Mockito.mock(UserUpdateRequest.class);
//        UserUpdateRequest userUpdateRequest
//                = UserUpdateRequest.of("MALE", null, null, null, null, null, null, null);
        userService.updateUser(user, userUpdateRequest);

        // then
        verify(user).update(userUpdateRequest);
        // verify(userUpdateRequest).getEmail();
    }

    @Test
    void deleteUser_invalidNewPassword() {

        // given
        User user = Mockito.mock(User.class);
        when(user.getPassword()).thenReturn("password_");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when
        // then
        UserQuitRequest userQuitRequest = UserQuitRequest.of(1, null, "password");
        assertThrows(InvalidInputException.class,
                () -> userService.deleteUser(user, userQuitRequest));
    }

    @Test
    void deleteUser() {
        // user
        // userQuitRequest

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        when(user.getRole()).thenReturn(RoleType.MENTOR);

        // when
        UserQuitRequest userQuitRequest = Mockito.mock(UserQuitRequest.class);
        userService.deleteUser(user, userQuitRequest);

        // then
        // notification 삭제
        verify(notificationRepository).deleteByUser(user);
        // mentorService, menteeService delete
        // verify(user).getRole();
        verify(mentorService).deleteMentor(user);
        verify(menteeService).deleteMentee(user);
        // user quit
        verify(user).quit(any());
    }

    @Test
    void updateUserPassword_invalidNewPassword() {
        // user, userPasswordUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

        // when
        // then
        assertThrows(InvalidInputException.class,
                () -> userService.updateUserPassword(user, Mockito.mock(UserPasswordUpdateRequest.class)));
    }

    @Test
    void updateUserPassword() {

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        // when
        userService.updateUserPassword(user, Mockito.mock(UserPasswordUpdateRequest.class));
        // then
        verify(user).updatePassword(any());
    }

    @Test
    void updateUserImage() {
        // user, userImageUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.updateUserImage(user, Mockito.mock(UserImageUpdateRequest.class));
        // then
        verify(user).updateImage(any());
    }

    @Test
    void updateUserFcmToken() {
        // username, fcmToken

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.findByFcmToken(anyString())).thenReturn(Optional.empty());

        // when
        userService.updateUserFcmToken("user", "fcmToken");

        // then
        verify(user).updateFcmToken(any());

    }

    @Test
    void updateUserFcmToken_alreadyExistToken() {
        // username, fcmToken

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        User tokenUser = Mockito.mock(User.class);
        when(userRepository.findByFcmToken(anyString())).thenReturn(Optional.of(tokenUser));

        // user와 tokenUser의 username이 다른 경우
        when(tokenUser.getUsername()).thenReturn("tokenUser");

        // when
        userService.updateUserFcmToken("user", "fcmToken");

        // then
        verify(tokenUser).updateFcmToken(null);
        verify(user).updateFcmToken(anyString());
    }
}