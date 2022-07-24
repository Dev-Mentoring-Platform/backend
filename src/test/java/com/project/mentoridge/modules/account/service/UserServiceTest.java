package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.InvalidInputException;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.enums.GenderType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        User user1 = User.builder()
                .username("user1@email.com")
                .name("user1")
                .gender(GenderType.FEMALE)
                .nickname("user1")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        User user2 = User.builder()
                .username("user2@email.com")
                .name("user2")
                .gender(GenderType.MALE)
                .nickname("user2")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(user1, user2)));
        // when
        Page<UserResponse> response = userService.getUserResponses(1);
        // then
        assertThat(response.getContent()).hasSize(2);
    }

    @Test
    void getUserResponse_by_userId() {

        // given
        User user = User.builder()
                .username("user@email.com")
                .name("user")
                .gender(GenderType.MALE)
                .birthYear("20220101")
                .phoneNumber("01012345678")
                .nickname("user")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        UserResponse response = userService.getUserResponse(1L);
        // then
        assertAll(
                () -> assertThat(response.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(response.getUsername()).isEqualTo(user.getUsername()),
                () -> assertThat(response.getRole()).isEqualTo(user.getRole()),
                () -> assertThat(response.getName()).isEqualTo(user.getName()),
                () -> assertThat(response.getGender()).isEqualTo(user.getGender()),
                () -> assertThat(response.getBirthYear()).isEqualTo(user.getBirthYear()),
                () -> assertThat(response.getPhoneNumber()).isEqualTo(user.getPhoneNumber()),
                () -> assertThat(response.getNickname()).isEqualTo(user.getNickname()),
                () -> assertThat(response.getImage()).isEqualTo(user.getImage()),
                () -> assertThat(response.getZone()).isEqualTo("서울특별시 강남구 청담동")
        );
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
        // verify(userLogService).update(eq(user), any(User.class), any(User.class));
    }

/*  => Controller에서 체크
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
    }*/

    @Test
    void deleteUser_invalidNewPassword() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("_password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserQuitRequest userQuitRequest = mock(UserQuitRequest.class);
        when(userQuitRequest.getPassword()).thenReturn("password");
        when(bCryptPasswordEncoder.matches("password", "_password")).thenReturn(false);

        // when
        // then
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
        when(user.getPassword()).thenReturn("password");
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest userPasswordUpdateRequest = mock(UserPasswordUpdateRequest.class);
        when(userPasswordUpdateRequest.getPassword()).thenReturn("password");
        when(bCryptPasswordEncoder.matches("password", "password")).thenReturn(true);

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
        verify(user).updatePassword("encoded_new_password", userLogService);
        // verify(userLogService).updatePassword(eq(user), any(User.class), any(User.class));
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
        // verify(userLogService).updateImage(eq(user), any(User.class), any(User.class));
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
        // verify(userLogService).updateImage(eq(user), any(User.class), any(User.class));
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