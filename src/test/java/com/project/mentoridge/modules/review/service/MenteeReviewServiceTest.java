package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractServiceTest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.modules.base.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenteeReviewServiceTest extends AbstractServiceTest {

    @InjectMocks
    MenteeReviewService menteeReviewService;
    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    MentorReviewRepository mentorReviewRepository;
    @Mock
    MenteeReviewQueryRepository menteeReviewQueryRepository;
    @Mock
    MenteeReviewLogService menteeReviewLogService;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Test
    void get_paged_ReviewResponses_of_lecture() {

        // given
        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        menteeReviewService.getReviewResponsesOfLecture(1L, 1);
        // then
        verify(menteeReviewQueryRepository).findReviewsWithChildByLecture(eq(lecture), any(Pageable.class));
    }

    @Test
    void get_ReviewResponse_of_lecture() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();

        User mentorUser = mock(User.class);
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();

        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("title")
                .subTitle("subTitle")
                .introduce("introduce")
                .content("content")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.OFFLINE, SystemType.ONLINE))
                .thumbnail("thumbnail")
                .build();
        LecturePrice lecturePrice = LecturePrice.builder()
                .lecture(lecture)
                .isGroup(false)
                .pricePerHour(20000L)
                .timePerLecture(5)
                .numberOfLectures(10)
                .build();
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(2L);
        MenteeReview menteeReview = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(lecture)
                .build();
        when(menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, 1L)).thenReturn(Optional.of(menteeReview));
        MentorReview mentorReview = MentorReview.builder()
                .content("Good")
                .mentor(mentor)
                .parent(menteeReview)
                .build();
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.of(mentorReview));

        // when
        ReviewResponse response = menteeReviewService.getReviewResponseOfLecture(1L, 1L);
        // then
        assertThat(response.getEnrollmentId()).isEqualTo(2L);
        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋아요");
        // child
        assertThat(response.getChild()).isNotNull();
        assertThat(response.getChild().getContent()).isEqualTo("Good");
    }

    @Test
    void get_paged_ReviewResponses_of_eachLecture() {

        // given
        // when
        menteeReviewService.getReviewResponsesOfEachLecture(1L, 1L, 1);
        // then
        verify(enrollmentRepository).findAllByLectureIdAndLecturePriceId(1L, 1L);
        verify(menteeReviewQueryRepository).findReviewsWithChildByLecturePrice(any(List.class), any(Pageable.class));
    }

    @Test
    void get_ReviewResponse_of_eachLecture() {

        // given
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(2L);
        MenteeReview menteeReview = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(mock(Lecture.class))
                .build();
        when(menteeReviewRepository.findMenteeReviewById(3L)).thenReturn(Optional.of(menteeReview));
        MentorReview mentorReview = MentorReview.builder()
                .content("Good")
                .mentor(mentor)
                .parent(menteeReview)
                .build();
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.of(mentorReview));

        // when
        ReviewResponse response = menteeReviewService.getReviewResponseOfEachLecture(1L, 1L, 3L);
        // then
        assertThat(response.getEnrollmentId()).isEqualTo(2L);
        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋아요");
        // child
        assertThat(response.getChild()).isNotNull();
        assertThat(response.getChild().getContent()).isEqualTo("Good");
    }

    @Test
    void get_ReviewResponse_of_enrollment() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(menteeUser);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(2L);
        when(enrollmentRepository.findById(2L)).thenReturn(Optional.of(enrollment));
        MenteeReview menteeReview = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(mock(Lecture.class))
                .build();
        when(menteeReviewRepository.findByEnrollmentAndId(enrollment, 3L)).thenReturn(Optional.of(menteeReview));
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.empty());

        // when
        ReviewResponse response = menteeReviewService.getReviewResponseOfEnrollment(1L, 2L, 3L);
        // then
        assertThat(response.getEnrollmentId()).isEqualTo(2L);
        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋아요");
        assertThat(response.getChild()).isNull();
    }

    @Test
    void get_paged_ReviewWithSimpleEachLectureResponses() {

        // given
        // when
        User user = mock(User.class);
        menteeReviewService.getReviewWithSimpleEachLectureResponses(user, 1);
        // then
        verify(menteeReviewQueryRepository).findReviewsWithChildAndSimpleEachLectureByUser(eq(user), any(Pageable.class));
    }

    @Test
    void get_ReviewResponse() {

        // given
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        Lecture lecture = mock(Lecture.class);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(2L);
        MenteeReview menteeReview = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(lecture)
                .build();
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(menteeReview));
        MentorReview mentorReview = MentorReview.builder()
                .content("Good")
                .mentor(mentor)
                .parent(menteeReview)
                .build();
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.of(mentorReview));

        // when
        ReviewResponse response = menteeReviewService.getReviewResponse(1L);
        // then
        assertThat(response.getEnrollmentId()).isEqualTo(2L);
        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋아요");
        // child
        assertThat(response.getChild()).isNotNull();
        assertThat(response.getChild().getContent()).isEqualTo("Good");
    }

    @Test
    void get_ReviewWithSimpleEachLectureResponse() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();

        User mentorUser = mock(User.class);
        Mentor mentor = Mentor.builder().user(mentorUser).build();

        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("title")
                .subTitle("subTitle")
                .introduce("introduce")
                .content("content")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.OFFLINE, SystemType.ONLINE))
                .thumbnail("thumbnail")
                .build();
       LecturePrice lecturePrice = LecturePrice.builder()
                .lecture(lecture)
                .isGroup(false)
                .pricePerHour(20000L)
                .timePerLecture(5)
                .numberOfLectures(10)
                .build();
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(2L);
        when(enrollment.getLecturePrice()).thenReturn(lecturePrice);

        MenteeReview menteeReview = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(lecture)
                .build();
        when(menteeReviewRepository.findByMenteeReviewId(1L)).thenReturn(menteeReview);
        MentorReview mentorReview = MentorReview.builder()
                .content("Good")
                .mentor(mentor)
                .parent(menteeReview)
                .build();
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.of(mentorReview));

        // when
        ReviewWithSimpleEachLectureResponse response = menteeReviewService.getReviewWithSimpleEachLectureResponse(1L);
        // then
        assertThat(response.getLecture().getTitle()).isEqualTo("title");
        assertThat(response.getEnrollmentId()).isEqualTo(2L);
        assertThat(response.getScore()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋아요");
        // child
        assertThat(response.getChild()).isNotNull();
        assertThat(response.getChild().getContent()).isEqualTo("Good");
    }

    @Test
    void createMenteeReview() {
        // user(mentee), enrollmentId, menteeReviewCreateRequest

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findEnrollmentWithLectureByEnrollmentId(1L)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);

        MenteeReviewCreateRequest menteeReviewCreateRequest = mock(MenteeReviewCreateRequest.class);
        MenteeReview menteeReview = mock(MenteeReview.class);
        when(menteeReviewCreateRequest.toEntity(mentee, enrollment.getLecture(), enrollment)).thenReturn(menteeReview);
        MenteeReview saved = mock(MenteeReview.class);
        when(menteeReviewRepository.save(menteeReview)).thenReturn(saved);

        // when
        menteeReviewService.createMenteeReview(menteeUser, 1L, menteeReviewCreateRequest);

        // then
        verify(menteeReviewRepository).save(any(MenteeReview.class));
        verify(menteeReviewLogService).insert(menteeUser, saved);
    }

    @Test
    void updateMenteeReview() {
        // user(mentee), lectureId, reviewId, menteeReviewUpdateRequest

        // given
        User menteeUser = mock(User.class);
        MenteeReview menteeReview = mock(MenteeReview.class);
        when(menteeReviewRepository.findById(1L)).thenReturn(Optional.of(menteeReview));

        // when
        MenteeReviewUpdateRequest menteeReviewUpdateRequest = mock(MenteeReviewUpdateRequest.class);
        menteeReviewService.updateMenteeReview(menteeUser, 1L, menteeReviewUpdateRequest);

        // then
        verify(menteeReview).update(menteeReviewUpdateRequest, menteeUser, menteeReviewLogService);
        // verify(menteeReviewLogService).update(eq(menteeUser), any(MenteeReview.class), any(MenteeReview.class));
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