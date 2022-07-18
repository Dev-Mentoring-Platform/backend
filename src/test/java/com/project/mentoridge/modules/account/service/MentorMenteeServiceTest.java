package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.repository.MentorQueryRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorMenteeServiceTest {

    @InjectMocks
    MentorMenteeService mentorMenteeService;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    MentorQueryRepository mentorQueryRepository;

    @Test
    void get_SimpleMenteeResponses_of_unclosed_lecture() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);
        // When
        mentorMenteeService.getSimpleMenteeResponses(mentorUser, false, true);
        // Then
        verify(mentorQueryRepository).findMenteesOfMentor(mentor, false, true);
    }

    @Test
    void get_SimpleMenteeResponses_of_closed_lecture() {

        // Given
        // When
        User mentorUser = mock(User.class);
        mentorMenteeService.getSimpleMenteeResponses(mentorUser, true, true);
        // Then
        verify(mentorRepository).findByUser(mentorUser);
        verify(mentorQueryRepository).findMenteesOfMentor(any(Mentor.class), eq(true), eq(true));
    }

    @Test
    void get_paged_MenteeEnrollmentInfoResponses() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // When
        mentorMenteeService.getMenteeLectureResponses(mentorUser, 1L, 1);
        // Then
        verify(mentorQueryRepository).findMenteeLecturesOfMentor(eq(mentor), eq(1L), any(Pageable.class));
    }

    @Test
    void get_MenteeEnrollmentInfoResponse() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // When
        mentorMenteeService.getMenteeLectureResponse(mentorUser, 1L, 2L);
        // Then
        verify(mentorQueryRepository).findMenteeLectureOfMentor(mentor, 1L, 2L);
    }

}