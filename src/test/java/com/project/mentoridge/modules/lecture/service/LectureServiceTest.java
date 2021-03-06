package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    LecturePriceRepository lecturePriceRepository;
    @Mock
    LectureSearchRepository lectureSearchRepository;
    @Mock
    LectureQueryRepository lectureQueryRepository;
    @Mock
    LectureLogService lectureLogService;
    @Mock
    LecturePriceLogService lecturePriceLogService;

    @Mock
    UserRepository userRepository;
    @Mock
    MenteeRepository menteeRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    PickRepository pickRepository;
    @Mock
    EnrollmentService enrollmentService;
    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    MenteeReviewRepository menteeReviewRepository;
    @Mock
    SubjectRepository subjectRepository;

    @Test
    void getLectureResponse() {

        // given
        // when
        User user = mock(User.class);
        lectureService.getLectureResponse(user, 1L);

        // then
        verify(lectureRepository).findById(1L);
        verify(lecturePriceRepository).findByLecture(any(Lecture.class));
        // setLectureReview
        verify(menteeReviewRepository).findByLecture(any(Lecture.class));
        // setLectureMentor
        verify(lectureRepository).findByMentor(any(Mentor.class));
        verify(menteeReviewRepository).countByLectureIn(any(List.class));
    }

    @Test
    void getEachLectureResponse() {

        // given
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
        lectureService.getEachLectureResponse(user, 1L, 1L);

        // then
        LecturePrice lecturePrice = mock(LecturePrice.class);
        verify(lecturePriceRepository).findByLectureIdAndLecturePriceId(1L, 1L);
        verify(lectureQueryRepository).findLectureReviewQueryDto(1L, 1L);
        // setLectureMentor
        verify(lectureRepository).findByMentor(any(Mentor.class));
        verify(menteeReviewRepository).countByLectureIn(any(List.class));
        // setPicked
        verify(pickRepository).findByMenteeAndLectureIdAndLecturePriceId(mentee, 1L, 1L);
    }

    @Test
    void get_paged_EachLectureResponses() {

        // given
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
        LectureListRequest lectureListRequest = mock(LectureListRequest.class);
        lectureService.getEachLectureResponses(user, "zone", lectureListRequest, 1);

        // then
        verify(lectureSearchRepository).findLecturePricesByZoneAndSearch(any(Address.class), eq(lectureListRequest), any(Pageable.class));
        verify(lectureQueryRepository).findLectureEnrollmentQueryDtoMap(any(List.class));
        verify(lectureQueryRepository).findLecturePickQueryDtoMap(any(List.class));

        verify(lectureQueryRepository).findLectureReviewQueryDtoMap(any(List.class), any(List.class));
        verify(lectureQueryRepository).findLectureMentorQueryDtoMap(any(List.class));

        // setPicked
        verify(pickRepository).findByMenteeAndLectureIdAndLecturePriceId(eq(mentee), anyLong(), anyLong());
    }

    @Test
    void createLecture() {
        // user(mentor), lectureCreateRequest

        // given
        User mentorUser = mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // when
        LectureCreateRequest lectureCreateRequest = Mockito.mock(LectureCreateRequest.class);
        lectureService.createLecture(mentorUser, lectureCreateRequest);

        // then
        verify(lectureRepository).save(lectureCreateRequest.toEntity(mentor));
        verify(lectureLogService).insert(eq(mentorUser), any(Lecture.class));
    }

    @DisplayName("?????? ????????? ????????? ?????? ??????")
    @Test
    void updateLecture_alreadyEnrolled() {
        // user(mentor), lectureId, lectureUpdateRequest

        // given
        User mentorUser = Mockito.mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.countByLecture(lecture)).thenReturn(2);

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> lectureService.updateLecture(mentorUser, 1L, Mockito.mock(LectureUpdateRequest.class)));
    }

    @Test
    void updateLecture() {
        // user(mentor), lectureId, lectureUpdateRequest

        // given
        User mentorUser = Mockito.mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.countByLecture(lecture)).thenReturn(0);

        // when
        LectureUpdateRequest lectureUpdateRequest = Mockito.mock(LectureUpdateRequest.class);
        lectureService.updateLecture(mentorUser, 1L, lectureUpdateRequest);

        // then
        verify(lecture).update(lectureUpdateRequest, subjectRepository, mentorUser, lectureLogService);
        // ????????? ????????? ????????? ??????
        verify(lecture).cancelApproval();
        verify(lectureLogService).update(eq(mentorUser), any(Lecture.class), any(Lecture.class));
    }

    @Test
    void deleteLecture() {
        // user, lectureId

        // given
        User mentorUser = Mockito.mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Lecture lecture = Mockito.mock(Lecture.class);
        when(lectureRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(lecture));
        List<Enrollment> enrollments = Arrays.asList(Mockito.mock(Enrollment.class), Mockito.mock(Enrollment.class));
        when(enrollmentRepository.findByLecture(lecture)).thenReturn(enrollments);

        // when
        lectureService.deleteLecture(mentorUser, 1L);

        // then
        verify(lecture).delete(mentorUser, lectureLogService);
        verify(enrollmentService, atLeast(enrollments.size())).deleteEnrollment(any(Enrollment.class));
        // pick
        verify(pickRepository).deleteByLecture(lecture);
        verify(lectureRepository).delete(lecture);
    }
/*
    @DisplayName("?????? ?????? ????????? ?????? ?????? ?????? ??????")
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

    @DisplayName("?????? ??????")
    @Test
    void admin_can_approve_lecture() {

        // given
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.ADMIN);
        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        lectureService.approve(user, 1L);

        // then
        verify(lecture).approve(lectureLogService);
        // verify(lectureLogService).approve(lecture);
    }

    @DisplayName("???????????? ?????? ?????? ??????")
    @Test
    void only_admin_can_approve_lecture() {

        // given
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.MENTOR);

//         Lecture lecture = mock(Lecture.class);
//         when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        // then
        assertThrows(UnauthorizedException.class, () -> lectureService.approve(user, 1L));
    }

    @DisplayName("?????? ????????? ????????? ?????? ??????")
    @Test
    void cannot_approve_alreadyApprovedLecture() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(user.getRole()).thenReturn(RoleType.ADMIN);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        Lecture lecture = mock(Lecture.class);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lecture.isApproved()).thenReturn(true);

        // when
        // then
        assertThrows(RuntimeException.class, () -> lectureService.approve(user, 1L));
    }

    @DisplayName("?????? ??????")
    @Test
    void open_lecture() {

        // given
        User mentorUser = mock(User.class);
        when(mentorUser.getUsername()).thenReturn("mentorUser@email.com");
        // ???????????? ??????
        when(mentorUser.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("mentorUser@email.com")).thenReturn(Optional.of(mentorUser));

        // ?????? ????????? ?????? ?????? ??????
//        Mentor mentor = mock(Mentor.class);
//        when(mentor.getUser()).thenReturn(mentorUser);

        Lecture lecture = mock(Lecture.class);
//        when(lecture.getMentor()).thenReturn(mentor);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        LecturePrice lecturePrice = mock(LecturePrice.class);
        when(lecturePriceRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(lecturePrice));

        // when
        lectureService.open(mentorUser, 1L, 1L);

        // then
        verify(lecturePrice).open(mentorUser, lecturePriceLogService);
        // verify(lecturePriceLogService).open(mentorUser, lecture, lecturePrice);
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void close_lecture() {

        // given
        User mentorUser = mock(User.class);
        when(mentorUser.getUsername()).thenReturn("mentorUser@email.com");
        // ???????????? ??????
        when(mentorUser.getRole()).thenReturn(RoleType.MENTOR);
        when(userRepository.findByUsername("mentorUser@email.com")).thenReturn(Optional.of(mentorUser));

        // ?????? ????????? ?????? ?????? ??????
//        Mentor mentor = mock(Mentor.class);
//        when(mentor.getUser()).thenReturn(mentorUser);

        Lecture lecture = mock(Lecture.class);
//        when(lecture.getMentor()).thenReturn(mentor);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        LecturePrice lecturePrice = mock(LecturePrice.class);
        when(lecturePriceRepository.findByLectureAndId(lecture, 1L)).thenReturn(Optional.of(lecturePrice));

        // when
        lectureService.close(mentorUser, 1L, 1L);
        // then
        verify(lecturePrice).close(mentorUser, lecturePriceLogService);
        // verify(lecturePriceLogService).close(mentorUser, lecture, lecturePrice);
    }
}
