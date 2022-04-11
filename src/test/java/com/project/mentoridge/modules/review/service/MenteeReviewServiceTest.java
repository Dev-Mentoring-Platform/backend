package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenteeReviewServiceTest {

    @InjectMocks
    MenteeReviewService menteeReviewService;
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
    void createMenteeReview() {
        // user(mentee), enrollmentId, menteeReviewCreateRequest
        // user(mentee), lectureId, menteeReviewCreateRequest

        // given
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);
        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLecture(mentee, lecture)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);

        // when
        User user = mock(User.class);
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(4, "good");
        menteeReviewService.createMenteeReview(user, 1L, menteeReviewCreateRequest);

        // then
        verify(menteeReviewRepository).save(menteeReviewCreateRequest.toEntity(mentee, lecture, enrollment));
    }

    @Test
    void updateMenteeReview() {
        // user(mentee), lectureId, reviewId, menteeReviewUpdateRequest

        // given
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLecture(mentee, lecture)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);

        MenteeReview review = mock(MenteeReview.class);
        when(menteeReviewRepository.findByEnrollmentAndId(enrollment, 1L)).thenReturn(Optional.of(review));

        // when
        User user = mock(User.class);
        MenteeReviewUpdateRequest menteeReviewUpdateRequest = mock(MenteeReviewUpdateRequest.class);
        menteeReviewService.updateMenteeReview(user, 1L, 1L, menteeReviewUpdateRequest);

        // then
        verify(review).updateMenteeReview(menteeReviewUpdateRequest);
    }

    @Test
    void deleteMenteeReview() {
        // user(mentee), lectureId, reviewId

        // given
        User user = getUserWithName("user");
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLecture(mentee, lecture)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);

        MenteeReview review = mock(MenteeReview.class);
        when(menteeReviewRepository.findByEnrollmentAndId(enrollment, 1L)).thenReturn(Optional.of(review));

        // when
        menteeReviewService.deleteMenteeReview(user, 1L, 1L);

        // then
        // 댓글 리뷰 삭제
        verify(review).delete();
        verify(menteeReviewRepository).delete(review);
    }

    @Test
    void getReviewResponse_withoutChild() {
        // reviewId

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        MenteeReview parent = menteeReviewCreateRequest.toEntity(mentee, mock(Lecture.class), mock(Enrollment.class));
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(mentorReviewRepository.findByParent(parent)).thenReturn(Optional.empty());

        // when
        // then
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponse(1L);
        System.out.println(reviewResponse);
    }

    @Test
    void getReviewResponse_withChild() {

        // given
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(4, "mentee content");
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        MenteeReview parent = menteeReviewCreateRequest.toEntity(mentee, mock(Lecture.class), mock(Enrollment.class));
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(parent));

        MentorReviewCreateRequest mentorReviewCreateRequest = getMentorReviewCreateRequestWithContent("mentor content");
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        MentorReview child = mentorReviewCreateRequest.toEntity(mentor, mock(Lecture.class), parent);
        when(mentorReviewRepository.findByParent(parent)).thenReturn(Optional.of(child));

        // when
        // then
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponse(1L);
        System.out.println(reviewResponse);
    }
}