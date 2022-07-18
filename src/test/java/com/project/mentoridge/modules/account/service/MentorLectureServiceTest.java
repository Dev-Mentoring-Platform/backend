package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorLectureServiceTest {

    @InjectMocks
    MentorLectureService mentorLectureService;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    LectureSearchRepository lectureSearchRepository;
    @Mock
    LectureQueryRepository lectureQueryRepository;

    @Test
    void get_paged_LectureResponses_by_user() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // When
        mentorLectureService.getLectureResponses(mentorUser, 1);
        // Then
        verify(lectureSearchRepository).findLecturesWithEnrollmentCountByMentor(eq(mentor), any(Pageable.class));
    }

    @Test
    void get_paged_LectureResponses_by_mentorId() {

        // Given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

        // When
        mentorLectureService.getLectureResponses(1L, 1);
        // Then
        verify(lectureRepository).findByMentor(eq(mentor), any(Pageable.class));
    }

    @Test
    void get_eachLectureResponse() {

        // Given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

        // When
        mentorLectureService.getEachLectureResponse(1L, 1L, 2L);
        // Then
        verify(lectureSearchRepository).findLecturePriceByMentor(mentor, 1L, 2L);
    }

    @Test
    void get_paged_eachLectureResponses() {

        // Given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

        Lecture lecture = mock(Lecture.class);
        LecturePrice lecturePrice1 = mock(LecturePrice.class);
        when(lecturePrice1.getLecture()).thenReturn(lecture);
        LecturePrice lecturePrice2 = mock(LecturePrice.class);
        when(lecturePrice2.getLecture()).thenReturn(lecture);
        Page<LecturePrice> lecturePrices = new PageImpl(Arrays.asList(lecturePrice1, lecturePrice2), PageRequest.of(0, 10, Sort.by("id").ascending()), 2);
        when(lectureSearchRepository.findLecturePricesByMentor(mentor, PageRequest.of(0, 10, Sort.by("id").ascending()))).thenReturn(lecturePrices);

        // When
        mentorLectureService.getEachLectureResponses(1L, 1);
        // Then
        verify(lectureSearchRepository).findLecturePricesByMentor(eq(mentor), any(Pageable.class));

        verify(lectureQueryRepository).findLectureEnrollmentQueryDtoMap(any(List.class));
        verify(lectureQueryRepository).findLecturePickQueryDtoMap(any(List.class));
        verify(lectureQueryRepository).findLectureReviewQueryDtoMap(any(List.class), any(List.class));
        verify(lectureQueryRepository).findLectureMentorQueryDtoMap(any(List.class));
    }

    @Test
    void get_paged_EnrollmentResponses() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // When
        mentorLectureService.getEnrollmentResponsesOfLecture(mentorUser, 1L, 1);
        // Then
        verify(lectureRepository).findByMentorAndId(mentor, 1L);
        verify(enrollmentRepository).findByLecture(any(Lecture.class), any(Pageable.class));
    }

    @Test
    void get_paged_MenteeResponses() {

        // Given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // When
        mentorLectureService.getEnrollmentResponsesOfLecture(mentorUser, 1L, 1);
        // Then
        verify(lectureRepository).findByMentorAndId(mentor, 1L);
        verify(enrollmentRepository).findByLecture(any(Lecture.class), any(Pageable.class));
    }

}