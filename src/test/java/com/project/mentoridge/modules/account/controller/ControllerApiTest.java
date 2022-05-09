package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Disabled
@Transactional
@MockMvcTest
public class ControllerApiTest {

    private static final String NAME = "user";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    UserService userService;
//    @Autowired
//    CareerService careerService;
//    @Autowired
//    EducationService educationService;
    @Autowired
    MentorService mentorService;
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

    @PostConstruct
    void init() {

        // address
        addressRepository.save(Address.builder()
                .state("서울특별시")
                .gu("종로구")
                .dongMyunLi("청운동")
                .build());
        addressRepository.save(Address.builder()
                .state("부산광역시")
                .gu("영도구")
                .dongMyunLi("봉래동")
                .build());
        // subject
//        subjectRepository.save(Subject.builder()
//                .subjectId(1L)
//                .krSubject("프론트엔드")
//                .learningKind(LearningKindType.IT)
//                .build());
//        subjectRepository.save(Subject.builder()
//                .subjectId(2L)
//                .krSubject("백엔드")
//                .learningKind(LearningKindType.IT)
//                .build());

        // user
        SignUpRequest signUpRequest1 = SignUpRequest.builder()
                .username("mentorUser@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("mentorUserName")
                .gender(GenderType.MALE)
                .birthYear("1990")
                .phoneNumber("01012345678")
                .nickname("mentorUserNickname")
                .zone("서울특별시 종로구 청운동")
                .build();
        User mentorUser = loginService.signUp(signUpRequest1);
        mentorUser.generateEmailVerifyToken();

        SignUpRequest signUpRequest2 = SignUpRequest.builder()
                .username("menteeUser@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName")
                .gender(GenderType.FEMALE)
                .birthYear("1995")
                .phoneNumber("01011112222")
                .nickname("menteeUserNickname")
                .zone("부산광역시 영도구 봉래동")
                .build();
        User menteeUser = loginService.signUp(signUpRequest2);
        menteeUser.generateEmailVerifyToken();

        // mentee
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        // career
        CareerCreateRequest careerCreateRequest = CareerCreateRequest.builder()
                .job("Devops Engineer")
                .companyName("쿠팡")
                .others("네이버(2020 ~ 2022)")
                .license("AWS")
                .build();
        // careerService.createCareer(mentorUser, careerCreateRequest);
        // education
        EducationCreateRequest educationCreateRequest = EducationCreateRequest.builder()
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("한국대학교")
                .major("컴퓨터공학과")
                .others("기계공학과")
                .build();
        // educationService.createEducation(mentorUser, educationCreateRequest);
        // mentor
        List<CareerCreateRequest> careerCreateRequests = new ArrayList<>();
        careerCreateRequests.add(careerCreateRequest);
        List<EducationCreateRequest> educationCreateRequests = new ArrayList<>();
        educationCreateRequests.add(educationCreateRequest);
        MentorSignUpRequest mentorSignUpRequest = MentorSignUpRequest.builder()
                .bio("안녕하세요! 만나서 반갑습니다.")
                .careers(careerCreateRequests)
                .educations(educationCreateRequests)
                .build();
        mentorService.createMentor(mentorUser, mentorSignUpRequest);


        // lecturePrice
        List<LectureCreateRequest.LecturePriceCreateRequest> lecturePriceCreateRequests = new ArrayList<>();
        lecturePriceCreateRequests.add(LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(10000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(10000L * 2 * 5)
                .build());
        lecturePriceCreateRequests.add(LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(false)
                .pricePerHour(20000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(20000L * 2 * 5)
                .build());
        // lectureSubject
        List<LectureCreateRequest.LectureSubjectCreateRequest> lectureSubjectCreateRequests = new ArrayList<>();
        lectureSubjectCreateRequests.add(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .subjectId(1L)
                .build());
        // lectureSystemType
        List<SystemType> systemTypes = new ArrayList<>();
        systemTypes.add(SystemType.ONLINE);
        systemTypes.add(SystemType.OFFLINE);
        // lecture
        LectureCreateRequest lectureCreateRequest = LectureCreateRequest.builder()
                .title("제목")
                .subTitle("부제목")
                .introduce("소개")
                .difficulty(DifficultyType.INTERMEDIATE)
                .content("내용")
                .systems(systemTypes)
                .lecturePrices(lecturePriceCreateRequests)
                .lectureSubjects(lectureSubjectCreateRequests)
                .build();
        lectureService.createLecture(mentorUser, lectureCreateRequest);

        // chatroom : 멘티가 멘토에게
        // chatroomService.createChatroomToMentor(menteeUser, 1L);

        // pick
        pickService.createPick(menteeUser, 1L, 1L);
        // enrollment
        enrollmentService.createEnrollment(menteeUser, 1L, 1L);

        // mentee_review

        // mentor_review

        // post
        // liking
        // comment

        // inquiry
        // notice
        // mentoridge_file

        // notification

    }

    @Test
    @WithAccount(NAME)
    void career_getCareer() {
        // @GetMapping("/{career_id}")

        // given
        // when
        // then
    }

    @Test
    @WithAccount(NAME)
    void education_getEducation() {
        // @GetMapping("/{education_id}")
    }

    @Test
    @WithAccount(NAME)
    void notice_getNotices() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void notice_getNotice() {
        // @GetMapping("/{notice_id}")
    }

    @Test
    @WithAccount(NAME)
    void menteeChatroom_getChatrooms() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void menteeChatroom_getMessagesOfChatroom() {
        // @GetMapping("/{chatroom_id}/messages")
    }

    @Test
    @WithAccount(NAME)
    void mentee_getMentees() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void mentee_getMentee() {
        // @GetMapping("/{mentee_id}")
    }

    @Test
    @WithAccount(NAME)
    void menteeLecture_getLectures() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void menteeLecture_getLecture() {
        // @GetMapping("/{lecture_id}/lecturePrices/{lecture_price_id}")
    }

    @Test
    @WithAccount(NAME)
    void menteeLecture_getReviewsOfLecture() {
        // @GetMapping("/{lecture_id}/reviews")
    }

    @Test
    @WithAccount(NAME)
    void menteeLecture_getReviewOfLecture() {
        // @GetMapping("/{lecture_id}/reviews/{mentee_review_id}")
    }

    @Test
    @WithAccount(NAME)
    void menteePick_getPicks() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void menteeReview_getReviews() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void menteeReview_getReview() {
        // @GetMapping("/{mentee_review_id}")
    }

    @Test
    @WithAccount(NAME)
    void menteeReview_getUnreviewedLecturesOfMentee() {
        // @GetMapping("/unreviewed")
    }

    @Test
    @WithAccount(NAME)
    void mentorChatroom_getChatrooms() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void mentorChatroom_getMessagesOfChatroom() {
        // @GetMapping("/{chatroom_id}/messages")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getMentors() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void mentor_getMyInfo() {
        // @GetMapping("/my-info")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getMentor() {
        // @GetMapping("/{mentor_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getCareers() {
        // @GetMapping("/{mentor_id}/careers")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getEducations() {
        // @GetMapping("/{mentor_id}/educations")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getLectures() {
        // @GetMapping("/{mentor_id}/lectures")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getLecture() {
        // @GetMapping("/{mentor_id}/lectures/{lecture_id}/lecturePrices/{lecture_price_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentor_getReviews() {
        // @GetMapping("/{mentor_id}/reviews")
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getLectures() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getLecture() {
        // @GetMapping("/{lecture_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getReviewsOfLecture() {
        // @GetMapping("/{lecture_id}/reviews")
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getReviewOfLecture() {
        // @GetMapping("/{lecture_id}/reviews/{mentee_review_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getMenteesOfLecture() {
        // @GetMapping("/{lecture_id}/mentees")
    }

    @Test
    @WithAccount(NAME)
    void mentorLecture_getEnrollmentsOfLecture() {
        // @GetMapping("/{lecture_id}/enrollments")
    }

    @Test
    @WithAccount(NAME)
    void mentorMentee_getMyMentees() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void mentorMentee_getMyMenteesAndEnrollmentInfo() {
        // @GetMapping("/{mentee_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentorMentee_getReviewsOfMyMentee() {
        // @GetMapping("/{mentee_id}/lectures/{lecture_id}/reviews/{mentee_review_id}")
    }

    @Test
    @WithAccount(NAME)
    void mentorReview_getMyReviewsByMyMentees() {
        // @GetMapping("/by-mentees")
    }

    @Test
    @WithAccount(NAME)
    void notification_getNotifications() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void notification_countUncheckedNotifications() {
        // @GetMapping("/count-unchecked")
    }

    @Test
    @WithAccount(NAME)
    void user_getUsers() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void user_getUser() {
        // @GetMapping("/{user_id}")
    }

    @Test
    @WithAccount(NAME)
    void user_getMyInfo() {
        // @GetMapping("/my-info")
    }

    @Test
    @WithAccount(NAME)
    void user_getQuitReasons() {
        // @GetMapping("/quit-reasons")
    }

    @Test
    @WithAccount(NAME)
    void address_getStates() {
        // @GetMapping(value = "/states")
    }

    @Test
    @WithAccount(NAME)
    void address_getSiGunGus() {
        // @GetMapping(value = "/siGunGus")
    }

    @Test
    @WithAccount(NAME)
    void address_getDongs() {
        // @GetMapping(value = "/dongs")
    }

    @Test
    @WithAccount(NAME)
    void comment_getComments() {
        // @GetMapping("/{post_id}/comments")
    }

    @Test
    @WithAccount(NAME)
    void post_getPosts() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void post_getPost() {
        // @GetMapping("/{post_id}")
    }

    @Test
    @WithAccount(NAME)
    void userPost_getPostsOfUser() {
        // @GetMapping
    }

    @Test
    @WithAccount(NAME)
    void userPost_getPost() {
        // @GetMapping("/{post_id}")
    }

    @Test
    @WithAccount(NAME)
    void userPost_getCommentingPosts() {
        // @GetMapping("/commenting")
    }

    @Test
    @WithAccount(NAME)
    void userPost_getLikingPosts() {
        // @GetMapping("/liking")
    }

    @Test
    @WithAccount(NAME)
    void lecture_getLecturePerLecturePrice() {
        // @GetMapping(value = "/{lecture_id}/lecturePrices/{lecture_price_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    }

    @Test
    @WithAccount(NAME)
    void lecture_getReviewsOfLecture() {
        // @GetMapping("/{lecture_id}/reviews")
    }

    @Test
    @WithAccount(NAME)
    void lecture_getReviewOfLecture() {
        // @GetMapping("/{lecture_id}/reviews/{mentee_review_id}")
    }

    @Test
    @WithAccount(NAME)
    void subject_getLearningKinds() {
        // @GetMapping(value = "/api/learningKinds", produces = MediaType.APPLICATION_JSON_VALUE)
    }

    @Test
    @WithAccount(NAME)
    void subject_getSubjects() {
        // @GetMapping(value = "/api/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    }

    @Test
    @WithAccount(NAME)
    void subject_getSubjects_() {
        // @GetMapping(value = "/api/learningKinds/{learning_kind}/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    }

    @Test
    @WithAccount(NAME)
    void login_getSessionUser() {
        // @GetMapping("/api/session-user")
    }

    @Test
    @WithAccount(NAME)
    void login_checkUsername() {
        // @GetMapping("/api/check-username")
    }

    @Test
    @WithAccount(NAME)
    void login_checkNickname() {
        // @GetMapping("/api/check-nickname")
    }

    @Test
    @WithAccount(NAME)
    void login_verifyEmail() {
        // @GetMapping("/api/verify-email")
    }

    @Test
    @WithAccount(NAME)
    void login_findPassword() {
        // @GetMapping("/api/find-password")
    }
}
