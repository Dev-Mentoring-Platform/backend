package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.chat.repository.ChatroomQueryRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class MentorServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorService mentorService;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    ChatroomQueryRepository chatroomQueryRepository;
    @Autowired
    MentorReviewRepository mentorReviewRepository;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    PickService pickService;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    ChatService chatService;

    private User menteeUser;
    private Mentee mentee;

    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
    }

    @Test
    void get_MentorResponse() {

        // given
        // when
        // then
        MentorResponse response = mentorService.getMentorResponse(mentorUser);
        assertAll(
                () -> assertThat(response).extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("user").extracting("userId").isEqualTo(mentorUser.getId()),
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(mentorUser.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(mentorUser.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(mentorUser.getName()),
                () -> assertThat(response).extracting("user").extracting("gender").isEqualTo(mentorUser.getGender()),
                () -> assertThat(response).extracting("user").extracting("birthYear").isEqualTo(mentorUser.getBirthYear()),
                () -> assertThat(response).extracting("user").extracting("phoneNumber").isEqualTo(mentorUser.getPhoneNumber()),
                () -> assertThat(response).extracting("user").extracting("nickname").isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response).extracting("user").extracting("image").isEqualTo(mentorUser.getImage()),
                () -> assertThat(response).extracting("user").extracting("zone").isEqualTo(mentorUser.getZone().toString()),
                () -> assertThat(response).extracting("bio").isEqualTo(mentor.getBio()),

                () -> assertThat(response).extracting("careers").isNotNull(),
                () -> assertThat(response).extracting("educations").isNotNull(),
                // 누적 멘티
                () -> assertThat(response).extracting("accumulatedMenteeCount").isEqualTo(0)
        );
    }

    @Test
    void createMentor_when_user_is_already_mentor() {

        // Given
        // When
        // Then
        assertThrows(AlreadyExistException.class,
                () -> mentorService.createMentor(mentorUser, mentorSignUpRequest));
    }

    @Test
    void Mentor_등록() {

        // Given
        // When
        mentorService.createMentor(menteeUser, mentorSignUpRequest);

        // Then
        Mentor mentor = mentorRepository.findByUser(menteeUser);
        assertNotNull(mentor);
        assertEquals(RoleType.MENTOR, menteeUser.getRole());
    }

    @Test
    void Mentor_수정() {

        // Given
        mentorService.createMentor(menteeUser, mentorSignUpRequest);

        // When
        mentorService.updateMentor(menteeUser, mentorUpdateRequest);

        // Then
        User updated = userRepository.findByUsername(menteeUser.getUsername()).orElseThrow(RuntimeException::new);
        assertEquals(RoleType.MENTOR, updated.getRole());

        Mentor mentor = mentorRepository.findByUser(updated);
        assertNotNull(mentor);
    }

    @Test
    void deleteMentor_when_exist_unfinished_enrollment() {

        // Given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        lecture.approve(lectureLogService);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser, mentor.getId());
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        // 강의 종료 X
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> mentorService.deleteMentor(mentorUser));
    }

    @Test
    void Mentor_탈퇴() {

        // Given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        lecture.approve(lectureLogService);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser, mentor.getId());
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());

        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());
        // 강의 종료
        enrollmentService.finish(menteeUser, enrollment.getId());

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        mentorService.deleteMentor(mentorUser);

        // Then
        User _mentorUser = userRepository.findByUsername(mentorUser.getUsername()).orElseThrow(RuntimeException::new);
        assertEquals(MENTEE, _mentorUser.getRole());
        assertAll(
                () -> assertThat(chatroomQueryRepository.findByMentorOrderByIdDesc(mentor).size()).isEqualTo(0),
                () -> assertThat(chatroomRepository.findByMentor(mentor).isEmpty()).isTrue(),

                () -> assertThat(lectureRepository.findByMentor(mentor).isEmpty()).isTrue(),
                () -> assertThat(pickRepository.findByLecture(lecture).isEmpty()).isTrue(),
                () -> assertThat(enrollmentRepository.findByLecture(lecture).isEmpty()).isTrue(),
                () -> assertThat(menteeReviewRepository.findByLecture(lecture).isEmpty()).isTrue(),

                () -> assertThat(mentorRepository.findById(mentor.getId()).isPresent()).isFalse()
        );;
    }

}