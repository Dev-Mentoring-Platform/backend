package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getMenteeReviewCreateRequestWithScoreAndContent;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenteeReviewServiceTest {

    @InjectMocks
    MenteeReviewService menteeReviewService;
    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    MenteeReviewLogService menteeReviewLogService;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Test
    void createMenteeReview() {
        // user(mentee), enrollmentId, menteeReviewCreateRequest

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findEnrollmentWithLectureByEnrollmentId(1L)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);
        when(enrollment.getLecture()).thenReturn(lecture);

        // when
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(4, "good");
        menteeReviewService.createMenteeReview(menteeUser, 1L, menteeReviewCreateRequest);

        // then
        verify(menteeReviewRepository).save(menteeReviewCreateRequest.toEntity(mentee, enrollment.getLecture(), enrollment));
        verify(menteeReviewLogService).insert(menteeUser, any(MenteeReview.class));
    }

    @Test
    void updateMenteeReview() {
        // user(mentee), lectureId, reviewId, menteeReviewUpdateRequest

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);

        MenteeReview menteeReview = mock(MenteeReview.class);
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(menteeReview));

        // when
        MenteeReviewUpdateRequest menteeReviewUpdateRequest = mock(MenteeReviewUpdateRequest.class);
        menteeReviewService.updateMenteeReview(menteeUser, 1L, menteeReviewUpdateRequest);

        // then
        verify(menteeReview).update(menteeReviewUpdateRequest, menteeUser, menteeReviewLogService);
        verify(menteeReviewLogService).update(eq(menteeUser), any(MenteeReview.class), any(MenteeReview.class));
    }

    @Test
    void deleteMenteeReview() {
        // user(mentee), lectureId, reviewId

        // given
        User menteeUser = getUserWithName("user");
//        Mentee mentee = mock(Mentee.class);
//        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        MenteeReview menteeReview = mock(MenteeReview.class);
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(menteeReview));

        // when
        menteeReviewService.deleteMenteeReview(menteeUser, 1L);

        // then
        // 댓글 리뷰 삭제
        verify(menteeReview).delete(menteeUser, menteeReviewLogService);
        // verify(menteeReviewLogService).delete(menteeUser, menteeReview);
        verify(menteeReviewRepository).delete(menteeReview);
    }
/*
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
    }*/
}