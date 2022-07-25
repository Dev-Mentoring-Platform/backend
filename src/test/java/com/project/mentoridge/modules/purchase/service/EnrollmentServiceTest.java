package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentQueryRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.project.mentoridge.modules.purchase.vo.Enrollment.buildEnrollment;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @InjectMocks
    EnrollmentServiceImpl enrollmentService;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    EnrollmentQueryRepository enrollmentQueryRepository;
    @Mock
    EnrollmentLogService enrollmentLogService;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    LecturePriceRepository lecturePriceRepository;
    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    MentorReviewRepository mentorReviewRepository;
    @Mock
    NotificationService notificationService;

    @Test
    void get_paged_EnrollmentWithEachLectureResponses_Of_Mentee() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        enrollmentService.getEnrollmentWithEachLectureResponsesOfMentee(menteeUser, false, 1);
        // then
        verify(enrollmentQueryRepository).findEnrollmentsWithEachLecture(eq(mentee), eq(false), any(Pageable.class));
    }

    @Test
    void get_EachLectureResponse_Of_Enrollment() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        enrollmentService.getEachLectureResponseOfEnrollment(menteeUser, 1L, true);
        // then
        verify(enrollmentQueryRepository).findEachLectureOfEnrollment(eq(mentee), eq(1L), eq(true));
    }

    @Test
    void get_paged_EnrollmentWithSimpleEachLectureResponses() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        enrollmentService.getEnrollmentWithSimpleEachLectureResponses(menteeUser, false, 1);
        // then
        verify(enrollmentQueryRepository).findEnrollments(eq(mentee), eq(false), any(Pageable.class));
    }

    @Test
    void get_EnrollmentWithSimpleEachLectureResponse() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        enrollmentService.getEnrollmentWithSimpleEachLectureResponse(menteeUser, 1L);
        // then
        verify(enrollmentQueryRepository).findEnrollment(mentee, 1L);
    }

    @Test
    void createEnrollment() {
        // user(mentee), lectureId, lecturePriceId

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mentorUser);

        Lecture lecture = mock(Lecture.class);
        LecturePrice lecturePrice = mock(LecturePrice.class);
        when(lecture.getMentor()).thenReturn(mentor);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lecturePriceRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(lecturePrice));
        // 강의 승인
        when(lecture.isApproved()).thenReturn(true);
        when(lecturePrice.isClosed()).thenReturn(false);

        // 동일 강의 재구매 불가
        when(enrollmentRepository.findByMenteeAndLectureAndLecturePrice(mentee, lecture, lecturePrice)).thenReturn(Optional.empty());

        // when
        enrollmentService.createEnrollment(menteeUser, 1L, 1L);

        // then
        Enrollment enrollment = buildEnrollment(mentee, lecture, lecturePrice);
        verify(enrollmentRepository).save(enrollment);

        Enrollment saved = mock(Enrollment.class);
        when(enrollmentRepository.save(enrollment)).thenReturn(saved);
        verify(enrollmentLogService).insert(menteeUser, saved);
        // 멘토에게 알림 전송
        verify(notificationService).createNotification(mentorUser, NotificationType.ENROLLMENT);
    }

    @DisplayName("이미 구매 이력이 있는 강의")
    @Test
    void createEnrollment_alreadyEnrolled() {

        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        LecturePrice lecturePrice = mock(LecturePrice.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lecturePriceRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(lecturePrice));
        // 강의 승인
        when(lecture.isApproved()).thenReturn(true);
        when(lecturePrice.isClosed()).thenReturn(false);

        // 종료/취소 내역 포함해서 조회
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLectureAndLecturePrice(mentee, lecture, lecturePrice)).thenReturn(Optional.of(enrollment));

        // when
        // then
        assertThrows(AlreadyExistException.class,
                () -> enrollmentService.createEnrollment(menteeUser, 1L, 1L));
        }
/*
    @Test
    void close() {
        // user, lectureId

        // given
        Mentee mentee = Mockito.mock(Mentee.class);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findById(anyLong())).thenReturn(Optional.of(lecture));

        Enrollment enrollment = Mockito.mock(Enrollment.class);
        when(enrollmentRepository.findByMenteeAndLecture(any(Mentee.class), any(Lecture.class)))
                .thenReturn(Optional.of(enrollment));

        // when
        User user = Mockito.mock(User.class);
        enrollmentService.close(user, 1L);

        // then
        verify(enrollment).close();
    }*/

    // user가 직접 enrollment를 삭제하는 것은 불가
    // 취소나 종료만 가능
    @Test
    void deleteEnrollment_noReview() {

        // given
        Enrollment enrollment = mock(Enrollment.class);
        when(menteeReviewRepository.findByEnrollment(enrollment)).thenReturn(null);

        // when
        enrollmentService.deleteEnrollment(enrollment);

        // then
        verify(enrollment).delete();
        // enrollment 전체에서 확인
        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void deleteEnrollment_withReviews() {

        // given
        Enrollment enrollment = mock(Enrollment.class);
        MenteeReview menteeReview = mock(MenteeReview.class);
        when(menteeReviewRepository.findByEnrollment(enrollment)).thenReturn(menteeReview);
        MentorReview mentorReview = mock(MentorReview.class);
        when(mentorReviewRepository.findByParent(menteeReview)).thenReturn(Optional.of(mentorReview));

        // when
        enrollmentService.deleteEnrollment(enrollment);

        // then
        verify(mentorReview).delete();
        verify(menteeReview).delete();
        verify(menteeReviewRepository).delete(menteeReview);
        verify(enrollment).delete();
        verify(enrollmentRepository).delete(enrollment);
    }

    @DisplayName("멘티 강의 신청을 멘토가 확인")
    @Test
    void check_enrollment() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        // when
        enrollmentService.check(mentorUser, 1L);

        // then
        verify(enrollment).check(mentorUser, enrollmentLogService);
        // verify(enrollmentLogService).check(eq(mentorUser), any(Enrollment.class));
    }
/*
    @DisplayName("멘티 강의 신청을 멘토가 확인 - 이미 신청 승인된 강의인 경우")
    @Test
    void check_already_checked_enrollment() {

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);

        // when
        // then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.check(mentorUser, 1L);
        });
    }*/

    @Test
    void finish_enrollment() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getMentee()).thenReturn(mentee);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        // when
        enrollmentService.finish(menteeUser, 1L);

        // then
        verify(enrollment).finish(menteeUser, enrollmentLogService);
        // verify(enrollmentLogService).finish(menteeUser, any(Enrollment.class));
    }

/*
    @Test
    void finish_already_finished_enrollment() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getMentee()).thenReturn(mentee);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollment.isChecked()).thenReturn(true);
        when(enrollment.isFinished()).thenReturn(true);

        // when
        // then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.finish(menteeUser, 1L);
        });
    }*/
}