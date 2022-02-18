package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.purchase.repository.CancellationQueryRepository;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorCancellationServiceTest {

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
    ChatService chatService;

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
        Cancellation cancellation = Cancellation.of(enrollment, "reason");
        when(cancellationRepository.findById(1L)).thenReturn(Optional.of(cancellation));

        // when
        mentorCancellationService.approve(user, 1L);

        // then
        verify(enrollment).cancel();
        // 채팅방 삭제
        verify(chatService).deleteChatroom(any(Enrollment.class));
        // verify(chatroomRepository).deleteByEnrollment(any(Enrollment.class));

        // TODO - 환불
    }
}