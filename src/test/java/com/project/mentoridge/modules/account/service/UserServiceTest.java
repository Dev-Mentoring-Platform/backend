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
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.log.component.UserLogService;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserQuitRequestWithReasonIdAndReasonAndPassword;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    InquiryRepository inquiryRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    LikingRepository likingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;

    @Mock
    MentorService mentorService;
    @Mock
    MenteeService menteeService;
    @Mock
    UserLogService userLogService;

    @DisplayName("Converter 테스트")
    @Test
    void getUserResponse() {

        // given
        User user = getUserWithName("user");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUserResponse(1L);
        // then
        // 서울특별시 강남구 삼성동
        assertThat(response.getZone()).isEqualTo("서울특별시 강남구 삼성동");
    }

    @Test
    void updateUser() {
        // user, userUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserUpdateRequest userUpdateRequest = Mockito.mock(UserUpdateRequest.class);
        userService.updateUser(user, userUpdateRequest);

        // then
        verify(user).update(userUpdateRequest, userLogService);
        verify(userLogService).update(user, any(User.class), user);
    }

    @Test
    void deleteUser_invalidNewPassword() {

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(user.getPassword()).thenReturn("password_");
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when
        // then
        UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(1, null, "password");
        assertThrows(InvalidInputException.class,
                () -> userService.deleteUser(user, userQuitRequest));
    }

    @Test
    void deleteUser() {
        // user
        // userQuitRequest

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(user.getRole()).thenReturn(RoleType.MENTOR);

        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        // when
        UserQuitRequest userQuitRequest = Mockito.mock(UserQuitRequest.class);
        userService.deleteUser(user, userQuitRequest);

        // then
        // mentorService, menteeService delete
        verify(mentorService).deleteMentor(user);
        verify(menteeService).deleteMentee(user);
        // inquiry 삭제
        verify(inquiryRepository).deleteByUser(user);
        // notification 삭제
        verify(notificationRepository).deleteByUser(user);
        // message 삭제
        verify(messageRepository).deleteBySender(user);
        // 좋아요 삭제
        verify(likingRepository).deleteByUser(user);
        // 댓글 삭제
        verify(commentRepository).deleteByUser(user);
        // 글 삭제
        verify(postRepository).deleteByUser(user);

        // user quit
        verify(user).quit(any(String.class), userLogService);
        verify(userLogService).delete(user, user);
        assertThat(user.getRole()).isEqualTo(RoleType.MENTEE);
        // 로그아웃
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void updateUserPassword_invalidNewPassword() {
        // user, userPasswordUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest userPasswordUpdateRequest = Mockito.mock(UserPasswordUpdateRequest.class);
        when(userPasswordUpdateRequest.getPassword()).thenReturn("_password");
        when(bCryptPasswordEncoder.matches("password", "_password")).thenReturn(false);

        // when
        // then
        assertThrows(InvalidInputException.class,
                () -> userService.updateUserPassword(user, userPasswordUpdateRequest));
    }

    @Test
    void updateUserPassword() {

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest userPasswordUpdateRequest = Mockito.mock(UserPasswordUpdateRequest.class);
        when(userPasswordUpdateRequest.getPassword()).thenReturn("password");
        when(bCryptPasswordEncoder.matches("password", "password")).thenReturn(true);

        when(userPasswordUpdateRequest.getNewPassword()).thenReturn("new_password");
        when(bCryptPasswordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        // when
        userService.updateUserPassword(user, Mockito.mock(UserPasswordUpdateRequest.class));

        // then
        verify(user).updatePassword(userPasswordUpdateRequest.getNewPassword(), userLogService);
        assertThat(user.getPassword()).isEqualTo("encoded_new_password");
        verify(userLogService).updatePassword(user, any(User.class), user);
    }

    @Test
    void updateUserImage() {
        // user, userImageUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserImageUpdateRequest userImageUpdateRequest = Mockito.mock(UserImageUpdateRequest.class);
        when(userImageUpdateRequest.getImage()).thenReturn("new_image");
        userService.updateUserImage(user, userImageUpdateRequest);

        // then
        verify(user).updateImage(userImageUpdateRequest.getImage(), userLogService);
        assertThat(user.getImage()).isEqualTo("new_image");
        verify(userLogService).updateImage(user, any(User.class), user);
    }

    @Test
    void updateUserFcmToken() {
        // username, fcmToken

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        String fcmToken = "fcmToken";
        when(userRepository.findByFcmToken(fcmToken)).thenReturn(Optional.empty());

        // when
        userService.updateUserFcmToken("user", fcmToken);

        // then
        verify(user).updateFcmToken(fcmToken, userLogService);
        assertThat(user.getFcmToken()).isEqualTo(fcmToken);
        verify(userLogService).updateImage(user, any(User.class), user);
    }

    @Test
    void updateUserFcmToken_alreadyExistToken() {
        // username, fcmToken

        // given
        User user = Mockito.mock(User.class);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        User tokenUser = Mockito.mock(User.class);
        String fcmToken = "fcmToken";
        when(userRepository.findByFcmToken(fcmToken)).thenReturn(Optional.of(tokenUser));

        // user와 tokenUser의 username이 다른 경우
        when(tokenUser.getUsername()).thenReturn("tokenUser");

        // when
        userService.updateUserFcmToken("user", fcmToken);

        // then
        verify(tokenUser).updateFcmToken(null, userLogService);
        assertThat(tokenUser.getFcmToken()).isEqualTo(null);
        verify(user).updateFcmToken(fcmToken, userLogService);
        assertThat(user.getFcmToken()).isEqualTo(fcmToken);
    }
}