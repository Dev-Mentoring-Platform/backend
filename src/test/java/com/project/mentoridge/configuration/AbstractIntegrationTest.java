package com.project.mentoridge.configuration;

import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;

public abstract class AbstractIntegrationTest {

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

    protected User adminUser;
    protected User mentorUser;
    protected Mentor mentor;
    protected User menteeUser1;
    protected Mentee mentee1;
    protected User menteeUser2;
    protected Mentee mentee2;

    protected Lecture lecture1;
    protected LecturePrice lecturePrice1;
    protected LecturePrice lecturePrice2;

    protected Lecture lecture2;
    protected LecturePrice lecturePrice3;

    protected Lecture lecture3;
    protected LecturePrice lecturePrice4;

    protected Chatroom chatroom;
    protected Pick pick;
    protected Enrollment enrollment1;
    protected Enrollment enrollment2;
    protected Enrollment enrollment3;
    protected Enrollment enrollment4;
    protected Enrollment enrollment5;

    protected MenteeReview menteeReview1;
    protected MenteeReview menteeReview2;
    protected MenteeReview menteeReview3;
    protected MenteeReview menteeReview4;

    protected MentorReview mentorReview;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        adminUser = userRepository.save(User.builder()
                .username("adminUser")
                .password("password")
                .name("adminUserName")
                .nickname("adminUserNickname")
                .role(RoleType.ADMIN)
                .provider(null)
                .providerId(null)
                .build());
        // loginService.verifyEmail(adminUser.getUsername(), adminUser.getEmailVerifyToken());

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        menteeUser1 = saveMenteeUser(loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("menteeUser2@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName2")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("menteeUserNickname2")
                .build();
        menteeUser2 = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        mentee2 = menteeRepository.findByUser(menteeUser2);

        // 승인된 강의
        lecture1 = saveLecture(lectureService, mentorUser);
        lecturePrice1 = lecture1.getLecturePrices().get(0);
        lecturePrice2 = lecture1.getLecturePrices().get(1);
        // 강의 모집 종료
        lectureService.close(mentorUser, lecture1.getId(), lecturePrice2.getId());
        lectureService.approve(adminUser, lecture1.getId());

        List<LectureCreateRequest.LecturePriceCreateRequest> lecturePriceCreateRequests2 = new ArrayList<>();
        lecturePriceCreateRequests2.add(LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(true)
                .numberOfMembers(5)
                .pricePerHour(5000L)
                .timePerLecture(4)
                .numberOfLectures(5)
                .totalPrice(5000L * 4 * 5)
                .build());
        List<LectureCreateRequest.LectureSubjectCreateRequest> lectureSubjectCreateRequests2 = new ArrayList<>();
        lectureSubjectCreateRequests2.add(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .subjectId(2L)
                .build());
        List<SystemType> systemTypes2 = new ArrayList<>();
        systemTypes2.add(SystemType.OFFLINE);
        LectureCreateRequest lectureCreateRequest2 = LectureCreateRequest.builder()
                .title("제목2")
                .subTitle("부제목2")
                .introduce("소개2")
                .difficulty(DifficultyType.BASIC)
                .content("내용2")
                .systems(systemTypes2)
                .lecturePrices(lecturePriceCreateRequests2)
                .lectureSubjects(lectureSubjectCreateRequests2)
                .build();
        lecture2 = lectureService.createLecture(mentorUser, lectureCreateRequest2);
        lecturePrice3 = lecture2.getLecturePrices().get(0);
        lectureService.approve(adminUser, lecture2.getId());

        // 미승인 강의
        List<LectureCreateRequest.LecturePriceCreateRequest> lecturePriceCreateRequests3 = new ArrayList<>();
        lecturePriceCreateRequests3.add(LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(false)
                .pricePerHour(15000L)
                .timePerLecture(2)
                .numberOfLectures(3)
                .totalPrice(15000L * 2 * 3)
                .build());
        List<LectureCreateRequest.LectureSubjectCreateRequest> lectureSubjectCreateRequests3 = new ArrayList<>();
        lectureSubjectCreateRequests3.add(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .subjectId(1L)
                .build());
        List<SystemType> systemTypes3 = new ArrayList<>();
        systemTypes3.add(SystemType.ONLINE);
        LectureCreateRequest lectureCreateRequest3 = LectureCreateRequest.builder()
                .title("제목3")
                .subTitle("부제목3")
                .introduce("소개3")
                .difficulty(DifficultyType.BASIC)
                .content("내용3")
                .systems(systemTypes3)
                .lecturePrices(lecturePriceCreateRequests3)
                .lectureSubjects(lectureSubjectCreateRequests3)
                .build();
        lecture3 = lectureService.createLecture(mentorUser, lectureCreateRequest3);
        lecturePrice4 = lecture3.getLecturePrices().get(0);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser1, mentor.getId());
        chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        Long pickId = pickService.createPick(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        pick = pickRepository.findById(pickId).orElseThrow(RuntimeException::new);

        //////////////////////////////// lecture 1 ////////////////////////////////
        enrollment1 = saveEnrollment(enrollmentService, menteeUser1, lecture1, lecturePrice1);
        enrollmentService.check(mentorUser, enrollment1.getId());
        enrollmentService.finish(menteeUser1, enrollment1.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest1 = MenteeReviewCreateRequest.builder()
                .score(5)
                .content("Great")
                .build();
        menteeReview1 = menteeReviewService.createMenteeReview(menteeUser1, enrollment1.getId(), menteeReviewCreateRequest1);
        mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture1, menteeReview1);

        enrollment2 = saveEnrollment(enrollmentService, menteeUser1, lecture1, lecturePrice2);
        enrollmentService.check(mentorUser, enrollment2.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest2 = MenteeReviewCreateRequest.builder()
                .score(1)
                .content("Bad")
                .build();
        menteeReview2 = menteeReviewService.createMenteeReview(menteeUser1, enrollment2.getId(), menteeReviewCreateRequest2);

        enrollment3 = saveEnrollment(enrollmentService, menteeUser2, lecture1, lecturePrice1);
        enrollmentService.check(mentorUser, enrollment3.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest3 = MenteeReviewCreateRequest.builder()
                .score(3)
                .content("So so")
                .build();
        menteeReview3 = menteeReviewService.createMenteeReview(menteeUser2, enrollment3.getId(), menteeReviewCreateRequest3);
        ///////////////////////////////////////////////////////////////////////////

        //////////////////////////////// lecture 2 ////////////////////////////////
        enrollment4 = saveEnrollment(enrollmentService, menteeUser1, lecture2, lecturePrice3);
        enrollmentService.check(mentorUser, enrollment4.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest4 = MenteeReviewCreateRequest.builder()
                .score(3)
                .content("Not bad")
                .build();
        menteeReview4 = menteeReviewService.createMenteeReview(menteeUser1, enrollment4.getId(), menteeReviewCreateRequest4);

        // 신청 미승인
        enrollment5 = saveEnrollment(enrollmentService, menteeUser2, lecture2, lecturePrice3);
        ///////////////////////////////////////////////////////////////////////////
    }
}
