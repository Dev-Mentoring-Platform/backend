package com.project.mentoridge.modules.purchase.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancellationServiceTest {
/*
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

    @DisplayName("취소")
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
    }*/

}