package com.project.mentoridge.modules.account.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorCancellationServiceTest {
/*
    @InjectMocks
    MentorCancellationService mentorCancellationService;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    CancellationRepository cancellationRepository;
    @Mock
    CancellationQueryRepository cancellationQueryRepository;

    @Mock
    ChatroomRepository chatroomRepository;
    @Mock
    @SpyBean
    ChatroomService chatroomService;

    @DisplayName("멘토가 아닌 경우")
    @Test
    void getCancellationResponses_notMentor() {
        // UnauthorizedException

        // given
        User user = Mockito.mock(User.class);
        when(mentorRepository.findByUser(user)).thenReturn(null);

        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> mentorCancellationService.getCancellationResponses(user, 20));

    }

    // 강의 환불 내역
    @Test
    void getCancellationResponses() {
        // user

        // given
        User user = Mockito.mock(User.class);
        Mentor mentor = Mentor.builder()
                .user(user)
                .build();
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        mentorCancellationService.getCancellationResponses(user, 20);

        // then
        verify(cancellationQueryRepository).findCancellationsOfMentor(any(Mentor.class), any(PageRequest.class));
    }

    @Test
    void approve_notMentor() {
        // UnauthorizedException

        // given
        User user = Mockito.mock(User.class);
        when(mentorRepository.findByUser(user)).thenReturn(null);

        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> mentorCancellationService.approve(user, any(Long.class)));
    }

    @DisplayName("환불/취소 승인")
    @Test
    void approve() {
        // user, cancellationId

        // given
        User user = Mockito.mock(User.class);
        Mentor mentor = Mentor.builder()
                .user(user)
                .build();
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        Enrollment enrollment = Mockito.mock(Enrollment.class);
        Cancellation cancellation = Cancellation.builder()
                .enrollment(enrollment)
                .reason("reason")
                .build();
        when(cancellationRepository.findById(1L)).thenReturn(Optional.of(cancellation));

        // when
        mentorCancellationService.approve(user, 1L);

        // then
        verify(enrollment).cancel();
    }*/
}