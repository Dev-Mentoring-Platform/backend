package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
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
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ServiceTest
class MentorLectureServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorLectureService mentorLectureService;

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

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

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

        // 강의 모집 종료
        lectureService.close(mentorUser, lecture1.getId(), lecturePrice2.getId());
    }

    @Test
    void get_paged_LectureResponses_by_user() {

        // Given
        // When
        Page<LectureResponse> responses = mentorLectureService.getLectureResponses(mentorUser, 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("id").isEqualTo(lecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(lecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(lecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(2),
                () -> assertThat(response.getLecturePrices()).hasSize(2),
                () -> assertThat(response.getLectureSubjects()).hasSize(1),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(lecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isNull(),

                () -> assertThat(response).extracting("lectureMentor").extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("lectureMentor").extracting("lectureCount").isEqualTo(1),
                () -> assertThat(response).extracting("lectureMentor").extracting("reviewCount").isEqualTo(4),
                () -> assertThat(response).extracting("lectureMentor").extracting("nickname").isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response).extracting("lectureMentor").extracting("image").isEqualTo(mentorUser.getImage())
                // TODO - TEST
                // () -> assertThat(response).extracting("picked").isEqualTo(false)
        );
    }

    @Test
    void get_paged_LectureResponses_by_mentorId() {

        // Given
        // When
        Page<LectureResponse> responses = mentorLectureService.getLectureResponses(mentor.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("id").isEqualTo(lecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(lecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(lecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(2),
                () -> assertThat(response.getLecturePrices()).hasSize(2),
                () -> assertThat(response.getLectureSubjects()).hasSize(1),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(lecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isNull(),

                () -> assertThat(response).extracting("lectureMentor").extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("lectureMentor").extracting("lectureCount").isEqualTo(1),
                () -> assertThat(response).extracting("lectureMentor").extracting("reviewCount").isEqualTo(4),
                () -> assertThat(response).extracting("lectureMentor").extracting("nickname").isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response).extracting("lectureMentor").extracting("image").isEqualTo(mentorUser.getImage())
                // TODO - TEST
                // () -> assertThat(response).extracting("picked").isEqualTo(false)
        );
    }

    @Test
    void get_eachLectureResponse() {

        // Given
        // When
        EachLectureResponse response1 = mentorLectureService.getEachLectureResponse(mentor.getId(), lecture1.getId(), lecturePrice1.getId());
        EachLectureResponse response2 = mentorLectureService.getEachLectureResponse(mentor.getId(), lecture1.getId(), lecturePrice2.getId());
        EachLectureResponse response3 = mentorLectureService.getEachLectureResponse(mentor.getId(), lecture2.getId(), lecturePrice3.getId());

        // Then
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(2),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response1.getLectureSubjects()).hasSize(2),
                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response1.isApproved()).isTrue(),

                () -> assertThat(response1.getReviewCount()).isEqualTo(3),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response1.getPicked()).isNull(),
                () -> assertThat(response1.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(2),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response2.getLectureSubjects()).hasSize(2),
                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response2.isApproved()).isTrue(),

                () -> assertThat(response2.getReviewCount()).isEqualTo(3),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response2.getPicked()).isNull(),
                () -> assertThat(response2.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response3.getLectureId()).isEqualTo(lecture2.getId()),
                () -> assertThat(response3.getTitle()).isEqualTo(lecture2.getTitle()),
                () -> assertThat(response3.getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                () -> assertThat(response3.getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                () -> assertThat(response3.getContent()).isEqualTo(lecture2.getContent()),
                () -> assertThat(response3.getDifficulty()).isEqualTo(lecture2.getDifficulty()),
                () -> assertThat(response3.getSystems()).hasSize(2),

                () -> assertThat(response3.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLecturePrice().isGroup()).isEqualTo(lecturePrice3.isGroup()),
                () -> assertThat(response3.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice3.getNumberOfMembers()),
                () -> assertThat(response3.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice3.getPricePerHour()),
                () -> assertThat(response3.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice3.getTimePerLecture()),
                () -> assertThat(response3.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice3.getNumberOfLectures()),
                () -> assertThat(response3.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice3.getTotalPrice()),
                () -> assertThat(response3.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice3.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response3.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice3.getPricePerHour(), lecturePrice3.getTimePerLecture(), lecturePrice3.getNumberOfLectures())),
                () -> assertThat(response3.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response3.getLectureSubjects()).hasSize(2),
                () -> assertThat(response3.getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                () -> assertThat(response3.isApproved()).isTrue(),

                () -> assertThat(response3.getReviewCount()).isEqualTo(3),
                () -> assertThat(response3.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response3.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response3.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response3.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response3.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response3.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response3.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response3.getPicked()).isNull(),
                () -> assertThat(response3.getPickCount()).isEqualTo(1)
        );
    }

    @Test
    void get_paged_eachLectureResponses() {

        // Given
        // When
        Page<EachLectureResponse> responses = mentorLectureService.getEachLectureResponses(mentor.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);

        EachLectureResponse response1 = responses.getContent().get(0);
        EachLectureResponse response2 = responses.getContent().get(1);
        EachLectureResponse response3 = responses.getContent().get(2);
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(2),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response1.getLectureSubjects()).hasSize(2),
                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response1.isApproved()).isTrue(),

                () -> assertThat(response1.getReviewCount()).isEqualTo(3),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response1.getPicked()).isNull(),
                () -> assertThat(response1.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(2),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response2.getLectureSubjects()).hasSize(2),
                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response2.isApproved()).isTrue(),

                () -> assertThat(response2.getReviewCount()).isEqualTo(3),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response2.getPicked()).isNull(),
                () -> assertThat(response2.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response3.getLectureId()).isEqualTo(lecture2.getId()),
                () -> assertThat(response3.getTitle()).isEqualTo(lecture2.getTitle()),
                () -> assertThat(response3.getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                () -> assertThat(response3.getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                () -> assertThat(response3.getContent()).isEqualTo(lecture2.getContent()),
                () -> assertThat(response3.getDifficulty()).isEqualTo(lecture2.getDifficulty()),
                () -> assertThat(response3.getSystems()).hasSize(2),

                () -> assertThat(response3.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLecturePrice().isGroup()).isEqualTo(lecturePrice3.isGroup()),
                () -> assertThat(response3.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice3.getNumberOfMembers()),
                () -> assertThat(response3.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice3.getPricePerHour()),
                () -> assertThat(response3.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice3.getTimePerLecture()),
                () -> assertThat(response3.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice3.getNumberOfLectures()),
                () -> assertThat(response3.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice3.getTotalPrice()),
                () -> assertThat(response3.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice3.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response3.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice3.getPricePerHour(), lecturePrice3.getTimePerLecture(), lecturePrice3.getNumberOfLectures())),
                () -> assertThat(response3.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response3.getLectureSubjects()).hasSize(2),
                () -> assertThat(response3.getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                () -> assertThat(response3.isApproved()).isTrue(),

                () -> assertThat(response3.getReviewCount()).isEqualTo(3),
                () -> assertThat(response3.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response3.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response3.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response3.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response3.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response3.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response3.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response3.getPicked()).isNull(),
                () -> assertThat(response3.getPickCount()).isEqualTo(1)
        );
    }

    @Test
    void get_paged_EnrollmentResponses() {

        // Given
        // When
        Page<EnrollmentResponse> responses = mentorLectureService.getEnrollmentResponsesOfLecture(mentorUser, lecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);
        for (EnrollmentResponse response : responses) {
            if (response.getEnrollmentId().equals(enrollment1.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment1.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment2.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment2.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment3.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser2.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment3.getCreatedAt()));
            }
        }
    }

    @Test
    void get_paged_MenteeResponses() {

        // Given
        // When
        Page<MenteeResponse> responses = mentorLectureService.getMenteeResponsesOfLecture(mentorUser, lecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(2);
        for (MenteeResponse response : responses) {
            Long userId = response.getUser().getUserId();
            if (userId.equals(menteeUser1.getId())) {

                assertThat(response.getUser().getUserId()).isEqualTo(menteeUser1.getId());
                assertThat(response.getUser().getUsername()).isEqualTo(menteeUser1.getUsername());
                assertThat(response.getUser().getRole()).isEqualTo(menteeUser1.getRole());
                assertThat(response.getUser().getName()).isEqualTo(menteeUser1.getName());
                assertThat(response.getUser().getGender()).isEqualTo(menteeUser1.getGender());
                assertThat(response.getUser().getBirthYear()).isEqualTo(menteeUser1.getBirthYear());
                assertThat(response.getUser().getPhoneNumber()).isEqualTo(menteeUser1.getPhoneNumber());
                assertThat(response.getUser().getNickname()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getUser().getImage()).isEqualTo(menteeUser1.getImage());
                assertThat(response.getUser().getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(menteeUser1.getZone()));
                assertThat(response.getSubjects()).isEqualTo(mentee1.getSubjects());

            } else if (userId.equals(menteeUser2.getId())) {

                assertThat(response.getUser().getUserId()).isEqualTo(menteeUser2.getId());
                assertThat(response.getUser().getUsername()).isEqualTo(menteeUser2.getUsername());
                assertThat(response.getUser().getRole()).isEqualTo(menteeUser2.getRole());
                assertThat(response.getUser().getName()).isEqualTo(menteeUser2.getName());
                assertThat(response.getUser().getGender()).isEqualTo(menteeUser2.getGender());
                assertThat(response.getUser().getBirthYear()).isEqualTo(menteeUser2.getBirthYear());
                assertThat(response.getUser().getPhoneNumber()).isEqualTo(menteeUser2.getPhoneNumber());
                assertThat(response.getUser().getNickname()).isEqualTo(menteeUser2.getNickname());
                assertThat(response.getUser().getImage()).isEqualTo(menteeUser2.getImage());
                assertThat(response.getUser().getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(menteeUser2.getZone()));
                assertThat(response.getSubjects()).isEqualTo(mentee2.getSubjects());
            }
        }
    }

}