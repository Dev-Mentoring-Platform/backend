package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenteeServiceTest {

    @InjectMocks
    MenteeService menteeService;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    PickRepository pickRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void getMenteeResponse() {
        // menteeId

        // given
        User user = User.of("user1@email.com", "password", "user1", null, null,
                null, "user1@email.com", "user1", null, null, null, RoleType.MENTEE, null, null);
        Mentee mentee = Mentee.of(user);
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));

        // when
        MenteeResponse response = menteeService.getMenteeResponse(1L);
        // then
        assertAll(
                () -> assertThat(response).extracting("user").extracting("email").isEqualTo("user1@email.com"),
                () -> assertThat(response).extracting("subjects").isEqualTo(mentee.getSubjects())
        );
    }

    @Test
    void updateMentee_notExist() {
        // 로그인 된 상태이므로
        // UnauthorizedException 발생

        // given
        User user = Mockito.mock(User.class);
        when(menteeRepository.findByUser(user)).thenReturn(null);
            //.thenReturn(Optional.empty());
        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> menteeService.updateMentee(user, any(MenteeUpdateRequest.class))
        );
    }

    @Test
    void updateMentee() {
        // user, MenteeUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        Mentee mentee = Mockito.mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
        MenteeUpdateRequest menteeUpdateRequest = MenteeUpdateRequest.of("subjects");
        menteeService.updateMentee(user, menteeUpdateRequest);
        // then
        verify(mentee).update(menteeUpdateRequest);
        // verify(mentee, atLeastOnce()).setSubjects(anyString());
    }

    // TODO - 순서 고려
    @Test
    void deleteMentee() {
        // 탈퇴
        // user

        // given
        User user = Mockito.mock(User.class);
        Mentee mentee = Mockito.mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
        menteeService.deleteMentee(user);

        // then

        // pick 삭제
        verify(pickRepository).deleteByMentee(mentee);

        // review 삭제

        // cancellation 삭제

        // enrollment 삭제
        // verify(enrollmentRepository).deleteByMentee(mentee);

        // chatroom 삭제

        verify(menteeRepository).delete(mentee);

        // TODO - 배치 / 일괄 삭제
        // verify(userRepository).delete(user);
    }
}