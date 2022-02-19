package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {

    @InjectMocks
    LectureServiceImpl lectureService;
    @Mock
    LectureRepository lectureRepository;

    @Mock
    MentorRepository mentorRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    EnrollmentService enrollmentService;
    @Mock
    PickRepository pickRepository;

    @Test
    void createLecture() {
        // user(mentor), lectureCreateRequest

        // given
        User user = getUserWithName("user");
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        LectureCreateRequest lectureCreateRequest = Mockito.mock(LectureCreateRequest.class);
        lectureService.createLecture(user, lectureCreateRequest);

        // then
        verify(lectureRepository).save(lectureCreateRequest.toEntity(mentor));
    }

    @DisplayName("수강 등록된 강의는 수정 불가")
    @Test
    void updateLecture_alreadyEnrolled() {
        // user(mentor), lectureId, lectureUpdateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lectureRepository.findById(any(Long.class)))
        when(lectureRepository.findByMentorAndId(any(Mentor.class), any(Long.class)))
                .thenReturn(Optional.of(lecture));

        when(enrollmentRepository.countAllByLectureId(any(Long.class))).thenReturn(2);
        // when
        // then
        User user = Mockito.mock(User.class);
        assertThrows(RuntimeException.class,
                () -> lectureService.updateLecture(user, 1L, Mockito.mock(LectureUpdateRequest.class)));
    }

    @Test
    void updateLecture() {
        // user(mentor), lectureId, lectureUpdateRequest

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lectureRepository.findById(any(Long.class)))
        when(lectureRepository.findByMentorAndId(any(Mentor.class), any(Long.class)))
                .thenReturn(Optional.of(lecture));

        when(enrollmentRepository.countAllByLectureId(any(Long.class))).thenReturn(0);

        // when
        User user = Mockito.mock(User.class);
        LectureUpdateRequest lectureUpdateRequest = Mockito.mock(LectureUpdateRequest.class);
        lectureService.updateLecture(user, 1L, lectureUpdateRequest);
        // then
        verify(lecture).update(lectureUpdateRequest);
    }

    @Test
    void deleteLecture() {
        // user, lectureId

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lectureRepository.findById(any(Long.class)))
        when(lectureRepository.findByMentorAndId(any(Mentor.class), any(Long.class)))
                .thenReturn(Optional.of(lecture));

        Enrollment closedEnrollment = Mockito.mock(Enrollment.class);
        when(closedEnrollment.isClosed()).thenReturn(true);
        // when(closedEnrollment.isCanceled()).thenReturn(false);
        Enrollment canceledEnrollment = Mockito.mock(Enrollment.class);
        // when(canceledEnrollment.isClosed()).thenReturn(false);
        when(canceledEnrollment.isCanceled()).thenReturn(true);

        List<Enrollment> enrollments = Arrays.asList(closedEnrollment, canceledEnrollment);
        when(enrollmentRepository.findAllByLectureId(anyLong())).thenReturn(enrollments);

        // when
        User user = Mockito.mock(User.class);
        lectureService.deleteLecture(user, 1L);

        // then
        verify(enrollmentService, atLeast(enrollments.size())).deleteEnrollment(any(Enrollment.class));
        // pick
        verify(pickRepository).deleteByLecture(lecture);
        verify(lectureRepository).delete(lecture);
    }

    @DisplayName("진행 중인 강의가 있는 경우 삭제 불가")
    @Test
    void deleteLecture_alreadyEnrolled() {
        // user, lectureId

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        // when(lectureRepository.findById(any(Long.class)))
        when(lectureRepository.findByMentorAndId(any(Mentor.class), any(Long.class)))
                .thenReturn(Optional.of(lecture));

        Enrollment closedEnrollment = Mockito.mock(Enrollment.class);
        when(closedEnrollment.isClosed()).thenReturn(true);
        // when(closedEnrollment.isCanceled()).thenReturn(false);
        Enrollment canceledEnrollment = Mockito.mock(Enrollment.class);
        // when(canceledEnrollment.isClosed()).thenReturn(false);
        when(canceledEnrollment.isCanceled()).thenReturn(true);
        Enrollment enrollment = Mockito.mock(Enrollment.class);
        when(enrollment.isClosed()).thenReturn(false);
        when(enrollment.isCanceled()).thenReturn(false);

        when(enrollmentRepository.findAllByLectureId(anyLong())).thenReturn(
                Arrays.asList(closedEnrollment, canceledEnrollment, enrollment)
        );
        // when
        // then
        User user = Mockito.mock(User.class);
        assertThrows(RuntimeException.class,
                () -> lectureService.deleteLecture(user, 1L)
        );
    }

}
