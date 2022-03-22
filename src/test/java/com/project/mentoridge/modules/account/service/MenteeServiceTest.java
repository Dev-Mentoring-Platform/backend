package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getMenteeUpdateRequestWithSubjects;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
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
    @Mock
    MenteeLogService menteeLogService;

    @Test
    void getMenteeResponse() {
        // menteeId

        // given
        User user = getUserWithName("user1");
        Mentee mentee = Mentee.builder()
                .user(user)
                .build();
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));

        // when
        MenteeResponse response = menteeService.getMenteeResponse(1L);
        // then
        assertAll(
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(user.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(user.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(user.getName()),
                () -> assertThat(response).extracting("user").extracting("birthYear").isEqualTo(user.getBirthYear()),
                () -> assertThat(response).extracting("user").extracting("phoneNumber").isEqualTo(user.getPhoneNumber()),
                () -> assertThat(response).extracting("user").extracting("nickname").isEqualTo(user.getNickname()),
                () -> assertThat(response).extracting("user").extracting("image").isEqualTo(user.getImage()),
                () -> assertThat(response).extracting("user").extracting("zone").isEqualTo(user.getZone().toString()),
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
        MenteeUpdateRequest menteeUpdateRequest = getMenteeUpdateRequestWithSubjects("subjects");
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