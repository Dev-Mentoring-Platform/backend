package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
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

    protected Lecture approvedLecture1;
    protected LecturePrice lecturePrice1OfApprovedLecture1;
    protected LecturePrice lecturePrice2OfApprovedLecture1;

    protected Lecture approvedLecture2;
    protected LecturePrice lecturePrice1OfApprovedLecture2;

    protected Lecture unapprovedLecture1;
    protected LecturePrice lecturePrice1OfUnapprovedLecture1;

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

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);

        menteeUser1 = saveMenteeUser("menteeUser1", loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);
        menteeUser2 = saveMenteeUser("menteeUser2", loginService);
        mentee2 = menteeRepository.findByUser(menteeUser2);

        // 승인된 강의
        approvedLecture1 = saveLectureWithTwoLecturePrices("lecture1", lectureService, mentorUser);
        lecturePrice1OfApprovedLecture1 = approvedLecture1.getLecturePrices().get(0);
        lecturePrice2OfApprovedLecture1 = approvedLecture1.getLecturePrices().get(1);
        lectureService.approve(adminUser, approvedLecture1.getId());

        approvedLecture2 = saveLectureWithOneLecturePrice("lecture2", lectureService, mentorUser);
        lecturePrice1OfApprovedLecture2 = approvedLecture2.getLecturePrices().get(0);
        lectureService.approve(adminUser, approvedLecture2.getId());

        // 미승인 강의
        unapprovedLecture1 = saveLectureWithOneLecturePrice("lecture3", lectureService, mentorUser);
        lecturePrice1OfUnapprovedLecture1 = unapprovedLecture1.getLecturePrices().get(0);

        // 채팅방 생성
        Long chatroomId = chatService.createChatroomByMentee(MENTEE.getType(), menteeUser1, mentor.getId());
        chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        Long pickId = pickService.createPick(menteeUser1, approvedLecture1.getId(), lecturePrice1OfApprovedLecture1.getId());
        pick = pickRepository.findById(pickId).orElseThrow(RuntimeException::new);

        //////////////////////////////// lecture 1 ////////////////////////////////
        enrollment1 = saveEnrollment(enrollmentService, menteeUser1, approvedLecture1, lecturePrice1OfApprovedLecture1);
        enrollmentService.check(mentorUser, enrollment1.getId());
        enrollmentService.finish(menteeUser1, enrollment1.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest1 = MenteeReviewCreateRequest.builder()
                .score(5)
                .content("Great")
                .build();
        menteeReview1 = menteeReviewService.createMenteeReview(menteeUser1, enrollment1.getId(), menteeReviewCreateRequest1);
        mentorReview = saveMentorReview(mentorReviewService, mentorUser, approvedLecture1, menteeReview1);

        enrollment2 = saveEnrollment(enrollmentService, menteeUser1, approvedLecture1, lecturePrice2OfApprovedLecture1);
        enrollmentService.check(mentorUser, enrollment2.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest2 = MenteeReviewCreateRequest.builder()
                .score(1)
                .content("Bad")
                .build();
        menteeReview2 = menteeReviewService.createMenteeReview(menteeUser1, enrollment2.getId(), menteeReviewCreateRequest2);

        enrollment3 = saveEnrollment(enrollmentService, menteeUser2, approvedLecture1, lecturePrice1OfApprovedLecture1);
        enrollmentService.check(mentorUser, enrollment3.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest3 = MenteeReviewCreateRequest.builder()
                .score(3)
                .content("So so")
                .build();
        menteeReview3 = menteeReviewService.createMenteeReview(menteeUser2, enrollment3.getId(), menteeReviewCreateRequest3);
        ///////////////////////////////////////////////////////////////////////////

        //////////////////////////////// lecture 2 ////////////////////////////////
        enrollment4 = saveEnrollment(enrollmentService, menteeUser1, approvedLecture2, lecturePrice1OfApprovedLecture2);
        enrollmentService.check(mentorUser, enrollment4.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest4 = MenteeReviewCreateRequest.builder()
                .score(3)
                .content("Not bad")
                .build();
        menteeReview4 = menteeReviewService.createMenteeReview(menteeUser1, enrollment4.getId(), menteeReviewCreateRequest4);

        // 신청 미승인
        enrollment5 = saveEnrollment(enrollmentService, menteeUser2, approvedLecture2, lecturePrice1OfApprovedLecture2);
        ///////////////////////////////////////////////////////////////////////////

        // 강의 모집 종료
        lectureService.close(mentorUser, approvedLecture1.getId(), lecturePrice2OfApprovedLecture1.getId());
    }

    @Test
    void get_paged_LectureResponses_by_user() {

        // Given
        // When
        Page<LectureResponse> responses = mentorLectureService.getLectureResponses(mentorUser, 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("lectureId").isEqualTo(approvedLecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(approvedLecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(approvedLecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(approvedLecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(approvedLecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(approvedLecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(approvedLecture1.getSystems().size()),
                () -> assertThat(response.getLecturePrices()).hasSize(approvedLecture1.getLecturePrices().size()),
                () -> assertThat(response.getLectureSubjects()).hasSize(approvedLecture1.getLectureSubjects().size()),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(approvedLecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(approvedLecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isEqualTo(1L),

                () -> assertThat(response).extracting("lectureMentor").isNull()
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
        assertThat(responses.getTotalElements()).isEqualTo(3);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("id").isEqualTo(approvedLecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(approvedLecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(approvedLecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(approvedLecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(approvedLecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(approvedLecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(approvedLecture1.getSystems().size()),
                () -> assertThat(response.getLecturePrices()).hasSize(approvedLecture1.getLecturePrices().size()),
                () -> assertThat(response.getLectureSubjects()).hasSize(approvedLecture1.getLectureSubjects().size()),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(approvedLecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(approvedLecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isNull(),

                () -> assertThat(response).extracting("lectureMentor").isNull()
                // TODO - TEST
                // () -> assertThat(response).extracting("picked").isEqualTo(false)
        );
    }

    @Test
    void get_eachLectureResponse() {

        // Given
        // When
        EachLectureResponse response1 = mentorLectureService.getEachLectureResponse(mentor.getId(), approvedLecture1.getId(), lecturePrice1OfApprovedLecture1.getId());
        EachLectureResponse response2 = mentorLectureService.getEachLectureResponse(mentor.getId(), approvedLecture1.getId(), lecturePrice2OfApprovedLecture1.getId());
        EachLectureResponse response3 = mentorLectureService.getEachLectureResponse(mentor.getId(), approvedLecture2.getId(), lecturePrice1OfApprovedLecture2.getId());

        // Then
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(approvedLecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(approvedLecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(approvedLecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(approvedLecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(approvedLecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(approvedLecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(approvedLecture1.getSystems().size()),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1OfApprovedLecture1.getId()),
                () -> assertThat(response1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1OfApprovedLecture1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1OfApprovedLecture1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1OfApprovedLecture1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1OfApprovedLecture1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1OfApprovedLecture1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1OfApprovedLecture1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1OfApprovedLecture1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1OfApprovedLecture1.getPricePerHour(), lecturePrice1OfApprovedLecture1.getTimePerLecture(), lecturePrice1OfApprovedLecture1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response1.getLectureSubjects()).hasSize(approvedLecture1.getLectureSubjects().size()),
                () -> assertThat(response1.getThumbnail()).isEqualTo(approvedLecture1.getThumbnail()),
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
                () -> assertThat(response2.getLectureId()).isEqualTo(approvedLecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(approvedLecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(approvedLecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(approvedLecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(approvedLecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(approvedLecture1.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(approvedLecture1.getSystems().size()),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2OfApprovedLecture1.getId()),
                () -> assertThat(response2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2OfApprovedLecture1.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2OfApprovedLecture1.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2OfApprovedLecture1.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2OfApprovedLecture1.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2OfApprovedLecture1.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2OfApprovedLecture1.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2OfApprovedLecture1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2OfApprovedLecture1.getPricePerHour(), lecturePrice2OfApprovedLecture1.getTimePerLecture(), lecturePrice2OfApprovedLecture1.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response2.getLectureSubjects()).hasSize(approvedLecture1.getLectureSubjects().size()),
                () -> assertThat(response2.getThumbnail()).isEqualTo(approvedLecture1.getThumbnail()),
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
                () -> assertThat(response3.getLectureId()).isEqualTo(approvedLecture2.getId()),
                () -> assertThat(response3.getTitle()).isEqualTo(approvedLecture2.getTitle()),
                () -> assertThat(response3.getSubTitle()).isEqualTo(approvedLecture2.getSubTitle()),
                () -> assertThat(response3.getIntroduce()).isEqualTo(approvedLecture2.getIntroduce()),
                () -> assertThat(response3.getContent()).isEqualTo(approvedLecture2.getContent()),
                () -> assertThat(response3.getDifficulty()).isEqualTo(approvedLecture2.getDifficulty()),
                () -> assertThat(response3.getSystems()).hasSize(approvedLecture2.getSystems().size()),

                () -> assertThat(response3.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1OfApprovedLecture2.getId()),
                () -> assertThat(response3.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1OfApprovedLecture2.isGroup()),
                () -> assertThat(response3.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1OfApprovedLecture2.getNumberOfMembers()),
                () -> assertThat(response3.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1OfApprovedLecture2.getPricePerHour()),
                () -> assertThat(response3.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1OfApprovedLecture2.getTimePerLecture()),
                () -> assertThat(response3.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1OfApprovedLecture2.getNumberOfLectures()),
                () -> assertThat(response3.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1OfApprovedLecture2.getTotalPrice()),
                () -> assertThat(response3.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1OfApprovedLecture2.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response3.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1OfApprovedLecture2.getPricePerHour(), lecturePrice1OfApprovedLecture2.getTimePerLecture(), lecturePrice1OfApprovedLecture2.getNumberOfLectures())),
                () -> assertThat(response3.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response3.getLectureSubjects()).hasSize(approvedLecture2.getLectureSubjects().size()),
                () -> assertThat(response3.getThumbnail()).isEqualTo(approvedLecture2.getThumbnail()),
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
        assertThat(responses.getTotalElements()).isEqualTo(2);
        EachLectureResponse response1 = responses.getContent().get(0);
        EachLectureResponse response2 = responses.getContent().get(1);
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(approvedLecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(approvedLecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(approvedLecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(approvedLecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(approvedLecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(approvedLecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(2),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1OfApprovedLecture1.getId()),
                () -> assertThat(response1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1OfApprovedLecture1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1OfApprovedLecture1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1OfApprovedLecture1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1OfApprovedLecture1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1OfApprovedLecture1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1OfApprovedLecture1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1OfApprovedLecture1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1OfApprovedLecture1.getPricePerHour(), lecturePrice1OfApprovedLecture1.getTimePerLecture(), lecturePrice1OfApprovedLecture1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response1.getLectureSubjects()).hasSize(approvedLecture1.getLectureSubjects().size()),
                () -> assertThat(response1.getThumbnail()).isEqualTo(approvedLecture1.getThumbnail()),
                () -> assertThat(response1.isApproved()).isTrue(),

                () -> assertThat(response1.getReviewCount()).isEqualTo(3),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response1.getLectureMentor()).isNull(),
                // TODO - TEST
                () -> assertThat(response1.getPicked()).isNull(),
                () -> assertThat(response1.getPickCount()).isEqualTo(1),

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response2.getLectureId()).isEqualTo(approvedLecture2.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(approvedLecture2.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(approvedLecture2.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(approvedLecture2.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(approvedLecture2.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(approvedLecture2.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(2),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1OfApprovedLecture2.getId()),
                () -> assertThat(response2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1OfApprovedLecture2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1OfApprovedLecture2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1OfApprovedLecture2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1OfApprovedLecture2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1OfApprovedLecture2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1OfApprovedLecture2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1OfApprovedLecture2.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1OfApprovedLecture2.getPricePerHour(), lecturePrice1OfApprovedLecture2.getTimePerLecture(), lecturePrice1OfApprovedLecture2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isFalse(),

                () -> assertThat(response2.getLectureSubjects()).hasSize(2),
                () -> assertThat(response2.getThumbnail()).isEqualTo(approvedLecture2.getThumbnail()),
                () -> assertThat(response2.isApproved()).isTrue(),

                () -> assertThat(response2.getReviewCount()).isEqualTo(3),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response2.getLectureMentor()).isNull(),
                // TODO - TEST
                () -> assertThat(response2.getPicked()).isNull(),
                () -> assertThat(response2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void get_paged_EnrollmentResponses() {

        // Given
        // When
        Page<EnrollmentResponse> responses = mentorLectureService.getEnrollmentResponsesOfLecture(mentorUser, approvedLecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);
        for (EnrollmentResponse response : responses) {
            if (response.getEnrollmentId().equals(enrollment1.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(approvedLecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment1.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment2.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(approvedLecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment2.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment3.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser2.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(approvedLecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment3.getCreatedAt()));
            }
        }
    }

    @Test
    void get_paged_MenteeResponses() {

        // Given
        // When
        Page<MenteeResponse> responses = mentorLectureService.getMenteeResponsesOfLecture(mentorUser, approvedLecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);
    }

}