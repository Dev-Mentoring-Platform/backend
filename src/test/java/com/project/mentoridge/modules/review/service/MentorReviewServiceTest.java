package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorReviewServiceTest {

    @InjectMocks
    MentorReviewService mentorReviewService;
    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    MentorReviewRepository mentorReviewRepository;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    MentorRepository mentorRepository;
    @Mock
    MentorReviewLogService mentorReviewLogService;
    @Mock
    MenteeReviewLogService menteeReviewLogService;

    @Test
    void createMentorReview() {
        // user(mentor), lectureId, parentId, mentorReviewCreateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = Mockito.mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));

        // when
        User user = Mockito.mock(User.class);
        MentorReviewCreateRequest mentorReviewCreateRequest = Mockito.mock(MentorReviewCreateRequest.class);
        mentorReviewService.createMentorReview(user, 1L, 1L, mentorReviewCreateRequest);

        // then
        verify(mentorReviewRepository).save(mentorReviewCreateRequest.toEntity(mentor, lecture, parent));
    }

    @Test
    void updateMentorReview() {
        // user(mentor), lectureId, parentId, reviewId, mentorReviewUpdateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = Mockito.mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        MentorReview review = Mockito.mock(MentorReview.class);
        when(mentorReviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        User user = Mockito.mock(User.class);
        MentorReviewUpdateRequest mentorReviewUpdateRequest = Mockito.mock(MentorReviewUpdateRequest.class);
        mentorReviewService.updateMentorReview(user, 1L, 1L, 2L, mentorReviewUpdateRequest);

        // then
        verify(review).updateMentorReview(mentorReviewUpdateRequest);
    }

    @Test
    void deleteMentorReview() {
        // user(mentor), lecturId, parentId, reviewId

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        MenteeReview parent = Mockito.mock(MenteeReview.class);
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        MentorReview review = Mockito.mock(MentorReview.class);
        when(mentorReviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        User user = Mockito.mock(User.class);
        mentorReviewService.deleteMentorReview(user, 1L, 1L, 2L);

        // then
        verify(mentorReviewRepository).delete(review);
    }

}