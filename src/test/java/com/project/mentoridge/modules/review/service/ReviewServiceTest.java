package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static com.project.mentoridge.modules.review.vo.Review.buildMenteeReview;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;
    @Mock
    ReviewRepository reviewRepository;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    MentorRepository mentorRepository;

    @Test
    void createMenteeReview() {
        // user(mentee), enrollmentId, menteeReviewCreateRequest
        // user(mentee), lectureId, menteeReviewCreateRequest

        // given
        Mentee mentee = Mockito.mock(Mentee.class);
        when(mentee.getId()).thenReturn(1L);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lecture.getId()).thenReturn(1L);
        when(lectureRepository.findById(anyLong())).thenReturn(Optional.of(lecture));

        Enrollment enrollment = Mockito.mock(Enrollment.class);
        when(enrollmentRepository.findAllByMenteeIdAndLectureId(1L, 1L))
                .thenReturn(Optional.of(enrollment));

        // when
        User user = Mockito.mock(User.class);
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(4, "good");
        reviewService.createMenteeReview(user, 1L, menteeReviewCreateRequest);

        // then
        verify(reviewRepository).save(buildMenteeReview(user, lecture, enrollment, menteeReviewCreateRequest));
    }

    @Test
    void updateMenteeReview() {
        // user(mentee), lectureId, reviewId, menteeReviewUpdateRequest

        // given
        Mentee mentee = Mockito.mock(Mentee.class);
        when(mentee.getId()).thenReturn(1L);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lecture.getId()).thenReturn(1L);
        // when(lectureRepository.findById(anyLong())).thenReturn(Optional.of(lecture));

        Enrollment enrollment = Mockito.mock(Enrollment.class);
        when(enrollmentRepository.findAllByMenteeIdAndLectureId(1L, 1L))
                .thenReturn(Optional.of(enrollment));

        Review review = Mockito.mock(Review.class);
        // when(review.getId()).thenReturn(1L);
        when(reviewRepository.findByEnrollmentAndId(enrollment, 1L))
                .thenReturn(Optional.of(review));

        // when
        User user = Mockito.mock(User.class);
        MenteeReviewUpdateRequest menteeReviewUpdateRequest = Mockito.mock(MenteeReviewUpdateRequest.class);
        reviewService.updateMenteeReview(user, 1L, 1L, menteeReviewUpdateRequest);

        // then
        verify(review).updateMenteeReview(menteeReviewUpdateRequest);
    }

    @Test
    void deleteMenteeReview() {
        // user(mentee), lectureId, reviewId

        // given
        User user = getUserWithName("user");
        Mentee mentee = Mockito.mock(Mentee.class);
        when(mentee.getId()).thenReturn(1L);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        Enrollment enrollment = Mockito.mock(Enrollment.class);
        when(enrollmentRepository.findAllByMenteeIdAndLectureId(1L, 1L)).thenReturn(Optional.of(enrollment));
        Review review = Mockito.mock(Review.class);
        when(reviewRepository.findByEnrollmentAndId(enrollment, 1L)).thenReturn(Optional.of(review));

        // when
        reviewService.deleteMenteeReview(user, 1L, 1L);

        // then
        // 댓글 리뷰 삭제
        verify(review).delete();
        verify(reviewRepository).delete(review);
    }

    @Test
    void createMentorReview() {
        // user(mentor), lectureId, parentId, mentorReviewCreateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        Review parent = Mockito.mock(Review.class);
        when(reviewRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));

        // when
        User user = Mockito.mock(User.class);
        MentorReviewCreateRequest mentorReviewCreateRequest = Mockito.mock(MentorReviewCreateRequest.class);
        reviewService.createMentorReview(user, 1L, 1L, mentorReviewCreateRequest);

        // then
        verify(reviewRepository).save(Review.buildMentorReview(user, lecture, parent, mentorReviewCreateRequest));
    }

    @Test
    void updateMentorReview() {
        // user(mentor), lectureId, parentId, reviewId, mentorReviewUpdateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));

        Review parent = Mockito.mock(Review.class);
        when(reviewRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        Review review = Mockito.mock(Review.class);
        when(reviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        User user = Mockito.mock(User.class);
        MentorReviewUpdateRequest mentorReviewUpdateRequest = Mockito.mock(MentorReviewUpdateRequest.class);
        reviewService.updateMentorReview(user, 1L, 1L, 2L, mentorReviewUpdateRequest);

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

        Review parent = Mockito.mock(Review.class);
        when(reviewRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(parent));
        Review review = Mockito.mock(Review.class);
        when(reviewRepository.findByParentAndId(parent, 2L)).thenReturn(Optional.of(review));

        // when
        User user = Mockito.mock(User.class);
        reviewService.deleteMentorReview(user, 1L, 1L, 2L);

        // then
        verify(review).delete();
        verify(reviewRepository).delete(review);
    }


    @Test
    void getReviewResponse_withoutChild() {
        // reviewId

        // given
        Review parent = buildMenteeReview(
                Mockito.mock(User.class),
                Mockito.mock(Lecture.class),
                Mockito.mock(Enrollment.class),
                menteeReviewCreateRequest
        );
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(reviewRepository.findByParent(parent)).thenReturn(Optional.empty());

        // when
        // then
        ReviewResponse reviewResponse = reviewService.getReviewResponse(1L);
        System.out.println(reviewResponse);
    }

    @Test
    void getReviewResponse_withChild() {

        // given
        Review parent = buildMenteeReview(
                Mockito.mock(User.class),
                Mockito.mock(Lecture.class),
                Mockito.mock(Enrollment.class),
                getMenteeReviewCreateRequestWithScoreAndContent(4, "mentee content")
        );
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(parent));

        Review child = Review.buildMentorReview(
                Mockito.mock(User.class),
                Mockito.mock(Lecture.class),
                parent,
                getMentorReviewCreateRequestWithContent("mentor content")
        );
        when(reviewRepository.findByParent(parent)).thenReturn(Optional.of(child));

        // when
        // then
        ReviewResponse reviewResponse = reviewService.getReviewResponse(1L);
        System.out.println(reviewResponse);
    }
}