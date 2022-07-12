package com.project.mentoridge.modules.purchase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class EnrollmentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceLogService lecturePriceLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    @BeforeEach
    void init() {

        // subject
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("백엔드")
                    .build());
            subjectRepository.save(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("프론트엔드")
                    .build());
        }

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        lecture.approve(lectureLogService);
    }

    @Test
    void cannot_enroll_unapproved_lecture() throws Exception {

        // Given
        lecture.cancelApproval();  // 승인 취소
        // When
        // Then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", lecture.getId(), lecturePrice.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        assertNull(enrollmentRepository.findAllByLectureIdAndLecturePriceId(lecture.getId(), lecturePrice.getId()));
    }

    @Test
    void cannot_enroll_closed_lecture() throws Exception {

        // Given
        lecturePrice.close(mentorUser, lecturePriceLogService);
        // When
        // Then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", lecture.getId(), lecturePrice.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        assertNull(enrollmentRepository.findAllByLectureIdAndLecturePriceId(lecture.getId(), lecturePrice.getId()));
    }

    @Test
    void can_enroll() throws Exception {

        // Given
        // When
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", lecture.getId(), lecturePrice.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        Enrollment enrollment = enrollmentRepository.findByMentee(mentee).get(0);
        assertAll(
                () -> assertNotNull(enrollment),
                () -> assertThat(enrollment.isChecked()).isFalse(),
                () -> assertThat(enrollment.getCheckedAt()).isNull(),
                () -> assertThat(enrollment.isFinished()).isFalse(),
                () -> assertThat(enrollment.getFinishedAt()).isNull(),

                () -> assertEquals(mentee, enrollment.getMentee()),
                // lecture
                () -> assertEquals(lecture, enrollment.getLecture()),
                () -> assertEquals(mentor, enrollment.getLecture().getMentor()),
                () -> assertEquals(lecture.getMentor(), enrollment.getLecture().getMentor()),
                () -> assertEquals(lecture.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(lecture.getSubTitle(), enrollment.getLecture().getSubTitle()),
                () -> assertEquals(lecture.getIntroduce(), enrollment.getLecture().getIntroduce()),
                () -> assertEquals(lecture.getContent(), enrollment.getLecture().getContent()),
                () -> assertEquals(lecture.getDifficulty(), enrollment.getLecture().getDifficulty()),
                () -> assertEquals(lecture.getSystems(), enrollment.getLecture().getSystems()),
                () -> assertEquals(lecture.getThumbnail(), enrollment.getLecture().getThumbnail()),
                () -> assertEquals(lecture.getLectureSubjects(), enrollment.getLecture().getLectureSubjects()),
                () -> assertEquals(lecture.isApproved(), enrollment.getLecture().isApproved()),
                // lecturePrice
                () -> assertEquals(lecturePrice.isGroup(), enrollment.getLecturePrice().isGroup()),
                () -> assertEquals(lecturePrice.getNumberOfMembers(), enrollment.getLecturePrice().getNumberOfMembers()),
                () -> assertEquals(lecturePrice.getPricePerHour(), enrollment.getLecturePrice().getPricePerHour()),
                () -> assertEquals(lecturePrice.getTimePerLecture(), enrollment.getLecturePrice().getTimePerLecture()),
                () -> assertEquals(lecturePrice.getNumberOfLectures(), enrollment.getLecturePrice().getNumberOfLectures()),
                () -> assertEquals(lecturePrice.getTotalPrice(), enrollment.getLecturePrice().getTotalPrice()),
                () -> assertEquals(lecturePrice.isClosed(), enrollment.getLecturePrice().isClosed())
        );
    }

    @DisplayName("강의 신청 확인")
    @Test
    void check_enrollment_by_mentor() throws Exception {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        // When
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/check", enrollment.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        Enrollment checked = enrollmentRepository.findById(enrollment.getId()).orElseThrow(RuntimeException::new);
        assertThat(checked.isChecked()).isTrue();
        assertThat(checked.getCheckedAt()).isNotNull();
    }

    @Test
    void check_already_checked_enrollment() throws Exception {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());
        // When
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/check", enrollment.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        // Then
        Enrollment checked = enrollmentRepository.findById(enrollment.getId()).orElseThrow(RuntimeException::new);
        assertThat(checked.isChecked()).isFalse();
        assertThat(checked.getCheckedAt()).isNull();
    }

    @Test
    void finish_unchecked_enrollment() throws Exception {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        // When
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", enrollment.getId())
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        // Then
        Enrollment finished = enrollmentRepository.findById(enrollment.getId()).orElseThrow(RuntimeException::new);
        assertThat(finished.isChecked()).isFalse();
        assertThat(finished.getCheckedAt()).isNull();
        assertThat(finished.isFinished()).isFalse();
        assertThat(finished.getFinishedAt()).isNull();
    }

    @DisplayName("수강 완료")
    @Test
    void finish_enrollment_by_mentee() throws Exception {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());
        // When
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        Enrollment finished = enrollmentRepository.findById(enrollment.getId()).orElseThrow(RuntimeException::new);
        assertThat(finished.isChecked()).isTrue();
        assertThat(finished.getCheckedAt()).isNotNull();
        assertThat(finished.isFinished()).isTrue();
        assertThat(finished.getFinishedAt()).isNotNull();
    }

    @Test
    void finish_already_finished_enrollment() throws Exception {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());
        enrollmentService.finish(menteeUser, enrollment.getId());

        // When
        // Then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", enrollment.getId())
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

}