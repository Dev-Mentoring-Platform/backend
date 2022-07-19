package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorReviewServiceTest {

    @InjectMocks
    MentorReviewService mentorReviewService;
    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    MentorReviewRepository mentorReviewRepository;
    @Mock
    MentorReviewQueryRepository mentorReviewQueryRepository;
    @Mock
    MentorReviewLogService mentorReviewLogService;

    @Mock
    MentorRepository mentorRepository;
    @Mock
    LectureRepository lectureRepository;


    @Test
    void getReview_WithSimpleEachLectureResponsesOfMentor_ByMentees() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // when
        mentorReviewService.getReviewWithSimpleEachLectureResponsesOfMentorByMentees(mentorUser, 1);
        // then
        verify(mentorReviewQueryRepository).findReviewsWithSimpleEachLectureOfMentorByMentees(eq(mentor), any(Pageable.class));
    }

    @Test
    void getReview_WithSimpleEachLectureResponsesOfMentor_ByMentees_by_mentorId() {

        // given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

        // when
        mentorReviewService.getReviewWithSimpleEachLectureResponsesOfMentorByMentees(1L, 1);
        // then
        verify(mentorReviewQueryRepository).findReviewsOfMentorByMentees(eq(mentor), any(Pageable.class));
    }

    @Test
    void createMentorReview() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));

        // when
        MentorReviewCreateRequest mentorReviewCreateRequest = mock(MentorReviewCreateRequest.class);
        MentorReview mentorReview = mock(MentorReview.class);
        when(mentorReviewCreateRequest.toEntity(mentor, parent)).thenReturn(mentorReview);
        mentorReviewService.createMentorReview(mentorUser, 1L, 1L, mentorReviewCreateRequest);

        // then
        verify(mentorReviewRepository).save(mentorReviewCreateRequest.toEntity(mentor, parent));
        verify(mentorReviewLogService).insert(eq(mentorUser), any(MentorReview.class));
    }

    @Test
    void updateMentorReview() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        MentorReview review = mock(MentorReview.class);
        when(mentorReviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        MentorReviewUpdateRequest mentorReviewUpdateRequest = mock(MentorReviewUpdateRequest.class);
        mentorReviewService.updateMentorReview(mentorUser, 1L, 1L, 2L, mentorReviewUpdateRequest);

        // then
        verify(review).update(mentorReviewUpdateRequest, mentorUser, mentorReviewLogService);
        // verify(mentorReviewLogService).update(eq(mentorUser), any(MentorReview.class), any(MentorReview.class));
    }

    @Test
    void deleteMentorReview() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        MentorReview review = mock(MentorReview.class);
        when(mentorReviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        mentorReviewService.deleteMentorReview(mentorUser, 1L, 1L, 2L);

        // then
        verify(review).delete(mentorUser, mentorReviewLogService);
        // verify(mentorReviewLogService).delete(eq(mentorUser), any(MentorReview.class));
        verify(mentorReviewRepository).delete(review);
    }

}