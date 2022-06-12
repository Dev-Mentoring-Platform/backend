package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MentorServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

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
    MentorReviewRepository mentorReviewRepository;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    LectureService lectureService;
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

    @WithAccount(NAME)
    @Test
    void get_MentorResponse() {

        // given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assert user != null;
        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);

        // when
        // then
        MentorResponse response = mentorService.getMentorResponse(user);
        assertAll(
                () -> assertThat(response).extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("user").extracting("userId").isEqualTo(user.getId()),
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(user.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(user.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(user.getName()),
                () -> assertThat(response).extracting("user").extracting("gender").isEqualTo(user.getGender().name()),
                () -> assertThat(response).extracting("user").extracting("birthYear").isEqualTo(user.getBirthYear()),
                () -> assertThat(response).extracting("user").extracting("phoneNumber").isEqualTo(user.getPhoneNumber()),
                () -> assertThat(response).extracting("user").extracting("nickname").isEqualTo(user.getNickname()),
                () -> assertThat(response).extracting("user").extracting("image").isEqualTo(user.getImage()),
                () -> assertThat(response).extracting("user").extracting("zone").isEqualTo(user.getZone().toString()),
                () -> assertThat(response).extracting("bio").isEqualTo(mentor.getBio()),

                () -> assertThat(response).extracting("careers").isOfAnyClassIn(CareerResponse.class),
                () -> assertThat(response).extracting("educations").isOfAnyClassIn(EducationResponse.class),
                // 누적 멘티
                () -> assertThat(response).extracting("accumulatedMenteeCount").isEqualTo(5)
        );
    }

    @WithAccount(NAME)
    @Test
    void createMentor_when_user_is_already_mentor() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        // Then
        assertThrows(AlreadyExistException.class,
                () -> mentorService.createMentor(user, mentorSignUpRequest));
    }

    @WithAccount(NAME)
    @Test
    void Mentor_등록() {

        // Given
        // When
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assert user != null;
        Mentor mentor = mentorRepository.findByUser(user);
        assertNotNull(mentor);
        assertEquals(RoleType.MENTOR, user.getRole());
    }

    @WithAccount(NAME)
    @Test
    void Mentor_수정() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mentorService.updateMentor(user, mentorUpdateRequest);

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assert user != null;
        assertEquals(RoleType.MENTOR, user.getRole());

        Mentor mentor = mentorRepository.findByUser(user);
        assertNotNull(mentor);
    }

    @Test
    void deleteMentor_when_exist_unfinished_enrollment() {

        // Given
        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        User mentorUser = saveMentorUser(loginService, mentorService);
        Mentor mentor = mentorRepository.findByUser(mentorUser);
        User menteeUser = saveMenteeUser(loginService);
        Mentee mentee = menteeRepository.findByUser(menteeUser);

        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser, mentor.getId());
        Chatroom chatroom = chatroomRepository.findById(chatroomId).orElse(null);
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        // 강의 종료 X
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        mentorService.deleteMentor(mentorUser);

        // Then
        assertThrows(RuntimeException.class,
                () -> mentorService.deleteMentor(mentorUser));
    }

    @Test
    void Mentor_탈퇴() {

        // Given
        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        User mentorUser = saveMentorUser(loginService, mentorService);
        Mentor mentor = mentorRepository.findByUser(mentorUser);
        User menteeUser = saveMenteeUser(loginService);
        Mentee mentee = menteeRepository.findByUser(menteeUser);

        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser, mentor.getId());
        Chatroom chatroom = chatroomRepository.findById(chatroomId).orElse(null);
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        // 강의 종료
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());
        enrollmentService.finish(menteeUser, enrollment.getId());
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        mentorService.deleteMentor(mentorUser);

        // Then
        User user = userRepository.findByUsername(mentorUser.getUsername()).orElse(null);
        assert user != null;
        assertEquals(MENTEE, user.getRole());
        assertAll(
                () -> assertThat(chatroomRepository.findByMentor(mentor).size()).isEqualTo(0),
                () -> assertFalse(chatroomRepository.findById(chatroom.getId()).isPresent()),
                () -> assertFalse(mentorReviewRepository.findById(mentorReview.getId()).isPresent()),
                () -> assertFalse(menteeReviewRepository.findById(menteeReview.getId()).isPresent()),
                () -> assertFalse(pickRepository.findById(pickId).isPresent()),
                () -> assertFalse(enrollmentRepository.findById(enrollment.getId()).isPresent()),
                () -> assertThat(lectureRepository.findByMentor(mentor).size()).isEqualTo(0)
        );;
    }

}