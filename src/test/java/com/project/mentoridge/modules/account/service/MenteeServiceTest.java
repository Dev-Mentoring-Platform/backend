package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenteeServiceTest {

    @InjectMocks
    MenteeService menteeService;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    ChatroomRepository chatroomRepository;
    @Mock
    PickRepository pickRepository;

    @Mock
    EnrollmentService enrollmentService;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    MenteeLogService menteeLogService;

    @Test
    void getMenteeResponses() {

        // given
        Page<Mentee> mentees = new PageImpl<>(Arrays.asList(mock(Mentee.class), mock(Mentee.class)));
        when(menteeRepository.findAll(any(Pageable.class))).thenReturn(mentees);
        // when
        menteeService.getMenteeResponses(1);
        // then
        verify(menteeRepository).findAll(any(Pageable.class));
    }

    @Test
    void getMenteeResponse() {
        // menteeId

        // given
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));
        // when
        menteeService.getMenteeResponse(1L);
        // then
        verify(menteeRepository).findById(1L);
    }

    @Test
    void _getMenteeResponse() {
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
                () -> assertThat(response).extracting("user").extracting("userId").isEqualTo(user.getId()),
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(user.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(user.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(user.getName()),
                () -> assertThat(response).extracting("user").extracting("gender").isEqualTo(user.getGender()),
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

        // given
        User user = mock(User.class);
        when(menteeRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> menteeService.updateMentee(user, any(MenteeUpdateRequest.class))
        );
    }

    @Test
    void updateMentee() {
        // user, MenteeUpdateRequest

        // given
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
//        MenteeUpdateRequest menteeUpdateRequest = MenteeUpdateRequest.builder()
//                .subjects("subjects")
//                .build();
        MenteeUpdateRequest menteeUpdateRequest = mock(MenteeUpdateRequest.class);
        menteeService.updateMentee(user, menteeUpdateRequest);

        // then
        verify(mentee).update(menteeUpdateRequest, user, menteeLogService);
        // verify(menteeLogService).update(any(User.class), any(Mentee.class), any(Mentee.class));
    }

    @DisplayName("멘티 탈퇴")
    @Test
    void deleteMentee() {
        // user

        // given
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        Enrollment enrollment1 = mock(Enrollment.class);
        Enrollment enrollment2 = mock(Enrollment.class);
        when(enrollmentRepository.findByMentee(mentee)).thenReturn(Arrays.asList(enrollment1, enrollment2));

        // when
        menteeService.deleteMentee(user);

        // then
        // chatroom 삭제
        verify(chatroomRepository).deleteByMentee(mentee);
        // pick 삭제
        verify(pickRepository).deleteByMentee(mentee);
        // enrollment 삭제
        verify(enrollmentRepository).findByMentee(mentee);
        verify(enrollmentService).deleteEnrollment(enrollment1);
        verify(enrollmentService).deleteEnrollment(enrollment2);
        verify(enrollmentService, atLeast(2)).deleteEnrollment(any(Enrollment.class));

        verify(mentee).delete(user, menteeLogService);
        // inject mock
        // verify(menteeLogService).delete(user, mentee);
        verify(menteeRepository).delete(mentee);
    }
}