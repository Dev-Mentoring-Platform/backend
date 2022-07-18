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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    UserLogService userLogService;
    @Mock
    MentorService mentorService;
    @Mock
    MenteeService menteeService;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    InquiryRepository inquiryRepository;
    @Mock
    LikingRepository likingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;

    @Test
    void getUserResponses() {

        // given
        // when
        userService.getUserResponses(1);
        // then
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void getUserResponse_by_userId() {

        // given
        // when
        userService.getUserResponse(1L);
        // then
        verify(userRepository).findById(1L);
    }

    @DisplayName("Converter 테스트")
    @Test
    void _getUserResponse() {

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
    void getUserResponse() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        userService.getUserResponse(user);
        // then
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser() {
        // user, userUpdateRequest

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserUpdateRequest userUpdateRequest = mock(UserUpdateRequest.class);
        userService.updateUser(user, userUpdateRequest);

        // then
        verify(user).update(userUpdateRequest, userLogService);
        verify(userLogService).update(eq(user), any(User.class), any(User.class));
    }

    @Test
    void deleteUser_withoutReason() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(user.getPassword()).thenReturn("password_");
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when
        // then
        UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(null, null, "password_");
        assertThrows(InvalidInputException.class,
                () -> userService.deleteUser(user, userQuitRequest));
    }

    @Test
    void deleteUser_invalidNewPassword() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when(user.getPassword()).thenReturn("password_");
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when
        // then
        // UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(1, null, "password");
        UserQuitRequest userQuitRequest = mock(UserQuitRequest.class);
        assertThrows(InvalidInputException.class,
                () -> userService.deleteUser(user, userQuitRequest));
    }

    @Test
    void deleteUser() {
        // user
        // userQuitRequest

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(user.getRole()).thenReturn(RoleType.MENTOR);

        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when
        UserQuitRequest userQuitRequest = mock(UserQuitRequest.class);
        userService.deleteUser(user, userQuitRequest);

        // then
        // mentorService, menteeService delete
        verify(mentorService).deleteMentor(user);
        verify(menteeService).deleteMentee(user);

        // inquiry 미삭제
        // verify(inquiryRepository).deleteByUser(user);

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
        verify(user).quit(anyString(), eq(userLogService));
        // verify(userLogService).delete(user, user);
        // assertThat(user.getRole()).isEqualTo(RoleType.MENTEE);
        // 로그아웃
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void updateUserPassword_invalidNewPassword() {
        // user, userPasswordUpdateRequest

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest userPasswordUpdateRequest = mock(UserPasswordUpdateRequest.class);
        when(userPasswordUpdateRequest.getPassword()).thenReturn("_password");
        when(bCryptPasswordEncoder.matches("_password", "password")).thenReturn(false);

        // when
        // then
        assertThrows(InvalidInputException.class,
                () -> userService.updateUserPassword(user, userPasswordUpdateRequest));
    }

    @Test
    void updateUserPassword() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest userPasswordUpdateRequest = mock(UserPasswordUpdateRequest.class);
        when(userPasswordUpdateRequest.getPassword()).thenReturn("password");
        when(bCryptPasswordEncoder.matches("password", "password")).thenReturn(true);

        when(userPasswordUpdateRequest.getNewPassword()).thenReturn("new_password");
        when(bCryptPasswordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        // when
        userService.updateUserPassword(user, userPasswordUpdateRequest);

        // then
        verify(user).updatePassword(userPasswordUpdateRequest.getNewPassword(), userLogService);
        assertThat(user.getPassword()).isEqualTo("encoded_new_password");
        verify(userLogService).updatePassword(eq(user), any(User.class), any(User.class));
    }

    @Test
    void updateUserImage() {
        // user, userImageUpdateRequest

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserImageUpdateRequest userImageUpdateRequest = mock(UserImageUpdateRequest.class);
        when(userImageUpdateRequest.getImage()).thenReturn("new_image");
        userService.updateUserImage(user, userImageUpdateRequest);

        // then
        verify(user).updateImage(userImageUpdateRequest.getImage(), userLogService);
        verify(userLogService).updateImage(eq(user), any(User.class), any(User.class));
    }

    @Test
    void updateUserFcmToken() {
        // username, fcmToken

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        String fcmToken = "fcmToken";
        when(userRepository.findByFcmToken(fcmToken)).thenReturn(Optional.empty());

        // when
        userService.updateUserFcmToken("user", fcmToken);

        // then
        verify(user).updateFcmToken(fcmToken, userLogService);
        verify(userLogService).updateImage(eq(user), any(User.class), any(User.class));
    }

    @Test
    void updateUserFcmToken_alreadyExistToken() {
        // username, fcmToken

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        User tokenUser = mock(User.class);
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
    }
}