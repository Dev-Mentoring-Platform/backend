package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.LectureLogService;
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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {

    @InjectMocks
    LectureServiceImpl lectureService;
    @Mock
    LectureRepository lectureRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    EnrollmentService enrollmentService;
    @Mock
    PickRepository pickRepository;
    @Mock
    LectureLogService lectureLogService;

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
        when(enrollmentRepository.countByLecture(any(Lecture.class))).thenReturn(2);

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
        when(enrollmentRepository.countByLecture(any(Lecture.class))).thenReturn(0);

        // when
        User user = Mockito.mock(User.class);
        LectureUpdateRequest lectureUpdateRequest = Mockito.mock(LectureUpdateRequest.class);
        lectureService.updateLecture(user, 1L, lectureUpdateRequest);
        // then
        // TODO - 수정된 강의는 재승인 필요
        fail();
        verify(lecture).update(lectureUpdateRequest);
    }

    @Test
    void deleteLecture() {
        // user, lectureId

        // given
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(any(User.class))).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(any(Mentor.class), any(Long.class))).thenReturn(Optional.of(lecture));
        List<Enrollment> enrollments = Arrays.asList(Mockito.mock(Enrollment.class), Mockito.mock(Enrollment.class));
        when(enrollmentRepository.findByLecture(any(Lecture.class))).thenReturn(enrollments);

        // when
        User user = Mockito.mock(User.class);
        lectureService.deleteLecture(user, 1L);

        // then
        verify(enrollmentService, atLeast(enrollments.size())).deleteEnrollment(any(Enrollment.class));
        // pick
        verify(pickRepository).deleteByLecture(lecture);
        verify(lectureRepository).delete(lecture);
    }
/*
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
    }*/

    @DisplayName("강의 승인")
    @Test
    void admin_can_approve_lecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(user.getRole()).thenReturn(RoleType.ADMIN);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        lectureService.approve(user, 1L);
        // then
        verify(lecture).approve();
    }

    @DisplayName("관리자만 강의 승인 가능")
    @Test
    void only_admin_can_approve_lecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

//         Lecture lecture = mock(Lecture.class);
//         when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        // then
        assertThrows(UnauthorizedException.class, () -> lectureService.approve(user, 1L));
    }

    @DisplayName("이미 승인된 강의는 승인 불가")
    @Test
    void cannot_approve_alreadyApprovedLecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(user.getRole()).thenReturn(RoleType.ADMIN);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Lecture lecture = mock(Lecture.class);
        when(lecture.isApproved()).thenReturn(true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        doCallRealMethod().when(lecture).approve();

        // when
        // then
        assertThrows(RuntimeException.class, () -> lectureService.approve(user, 1L));
    }

    @DisplayName("강의 모집 종료")
    @Test
    void close_lecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        // 멘토인지 확인
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // 본인 강의만 모집 종료 가능
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(user);

        Lecture lecture = mock(Lecture.class);
        when(lecture.getMentor()).thenReturn(mentor);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        lectureService.close(user, 1L);
        // then
        verify(lecture).close();
    }

    @DisplayName("강의 모집")
    @Test
    void open_lecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        // 멘토인지 확인
        when(user.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // 본인 강의만 모집 시작 가능
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(user);

        Lecture lecture = mock(Lecture.class);
        when(lecture.getMentor()).thenReturn(mentor);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        lectureService.open(user, 1L);
        // then
        verify(lecture).open();

    }

}
