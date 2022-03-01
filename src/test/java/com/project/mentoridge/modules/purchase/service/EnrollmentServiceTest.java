package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @InjectMocks
    EnrollmentServiceImpl enrollmentService;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    MenteeRepository menteeRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    LecturePriceRepository lecturePriceRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    NotificationService notificationService;

    @Test
    void createEnrollment() {
        // user(mentee), lectureId, lecturePriceId

        // given
        Mentee mentee = mock(Mentee.class);
        when(mentee.getId()).thenReturn(1L);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(user);

        Lecture lecture = mock(Lecture.class);
        when(lecture.getMentor()).thenReturn(mentor);
        when(lecture.getId()).thenReturn(1L);
        // 강의 승인
        when(lecture.isApproved()).thenReturn(true);
        when(lectureRepository.findById(anyLong())).thenReturn(Optional.of(lecture));

        LecturePrice lecturePrice = mock(LecturePrice.class);
        when(lecturePriceRepository.findByLectureAndId(any(Lecture.class), anyLong())).thenReturn(Optional.of(lecturePrice));

        when(enrollmentRepository.findAllByMenteeIdAndLectureId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // when
        enrollmentService.createEnrollment(user, 1L, 1L);

        // then
        verify(enrollmentRepository).save(any(Enrollment.class));

        // TODO - CHECK
        // 알림 전송
        // 푸시 알림 전송
        verify(notificationService).createNotification(user, NotificationType.ENROLLMENT);
    }

    @DisplayName("이미 구매 이력이 있는 강의")
    @Test
    void createEnrollment_alreadyEnrolled() {

        Mentee mentee = mock(Mentee.class);
        when(mentee.getId()).thenReturn(1L);
        when(menteeRepository.findByUser(any(User.class))).thenReturn(mentee);

        Lecture lecture = mock(Lecture.class);
        when(lecture.getId()).thenReturn(1L);
        // 강의 승인
        when(lecture.isApproved()).thenReturn(true);
        when(lectureRepository.findById(anyLong())).thenReturn(Optional.of(lecture));

        LecturePrice lecturePrice = mock(LecturePrice.class);
        // when(lecturePrice.getId()).thenReturn(1L);
        when(lecturePriceRepository.findByLectureAndId(any(Lecture.class), anyLong())).thenReturn(Optional.of(lecturePrice));

        // 종료/취소 내역 포함해서 조회
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollmentRepository.findAllByMenteeIdAndLectureId(anyLong(), anyLong())).thenReturn(Optional.of(enrollment));

        // when
        // then
        User user = mock(User.class);
        assertThrows(AlreadyExistException.class,
                () -> enrollmentService.createEnrollment(user, 1L, 1L));
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
        when(enrollment.getId()).thenReturn(1L);
        when(reviewRepository.findByEnrollment(enrollment)).thenReturn(null);

        // when
        enrollmentService.deleteEnrollment(enrollment);

        // then
        verify(enrollment).delete();
        // enrollment 전체에서 확인
        // verify(enrollmentRepository).delete(enrollment);
        verify(enrollmentRepository).deleteEnrollmentById(1L);
    }

    @Test
    void deleteEnrollment_withReview() {

        // given
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getId()).thenReturn(1L);
        Review review = mock(Review.class);
        when(reviewRepository.findByEnrollment(enrollment)).thenReturn(review);

        // when
        enrollmentService.deleteEnrollment(enrollment);

        // then
        verify(review).delete();
        verify(reviewRepository).delete(review);
        verify(enrollment).delete();
        verify(enrollmentRepository).deleteEnrollmentById(1L);
    }
}