package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancellationServiceTest {

    @InjectMocks
    CancellationServiceImpl cancellationService;
    @Mock
    CancellationRepository cancellationRepository;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @DisplayName("환불 요청")
    @Test
    void cancel() {
        // user(mentee), lectureId, cancellationCreateRequest

        // given
        User user = getUserWithName("user");
        Mentee mentee = Mentee.builder()
                .user(user)
                .build();
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // 해당 멘티가 수강 중인 강의인지 확인
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLectureAndCanceledFalseAndClosedFalse(mentee, lecture))
                .thenReturn(Optional.of(enrollment));

        // when
        CancellationCreateRequest cancellationCreateRequest = mock(CancellationCreateRequest.class);
        cancellationService.cancel(user, 1L, cancellationCreateRequest);

        // then
        verify(cancellationRepository).save(cancellationCreateRequest.toEntity(enrollment));
    }

}