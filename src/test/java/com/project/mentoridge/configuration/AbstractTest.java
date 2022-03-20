package com.project.mentoridge.configuration;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.*;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.service.ReviewService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractTest {

//    @Autowired
//    private FilterChainProxy springSecurityFilterChain;

//    protected MockMvc mockMvc;
//
//    @BeforeEach
//    private void setUp(WebApplicationContext webAppContext) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .build();
//    }

    @Autowired
    protected LoginService loginService;

    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MenteeService menteeService;
    @Autowired
    protected MenteeRepository menteeRepository;

    @Autowired
    protected CareerService careerService;
    @Autowired
    protected CareerRepository careerRepository;
    @Autowired
    protected EducationService educationService;
    @Autowired
    protected EducationRepository educationRepository;
    @Autowired
    protected MentorService mentorService;
    @Autowired
    protected MentorRepository mentorRepository;

    @Autowired
    protected LectureService lectureService;
    @Autowired
    protected LectureRepository lectureRepository;
    @Autowired
    protected LectureSearchRepository lectureSearchRepository;
    @Autowired
    protected LectureSubjectRepository lectureSubjectRepository;
    @Autowired
    protected LecturePriceRepository lecturePriceRepository;

    @Autowired
    protected PickService pickService;
    @Autowired
    protected PickRepository pickRepository;

    @Autowired
    protected EnrollmentService enrollmentService;
    @Autowired
    protected EnrollmentRepository enrollmentRepository;

    @Autowired
    protected ChatroomRepository chatroomRepository;

    @Autowired
    protected NotificationService notificationService;
    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected ReviewService reviewService;
    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected SubjectRepository subjectRepository;

    protected static final String NAME = "yk";
    protected static final String NICKNAME = NAME;
    protected static final String USERNAME = NAME + "@email.com";
    protected static final String EMAIL = USERNAME;

    public static SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname(NAME, NICKNAME);

    public Map<String, String> userInfo = getUserInfo(NAME, USERNAME);
    public static final SignUpOAuthDetailRequest signUpOAuthDetailRequest = getSignUpOAuthDetailRequestWithNickname(NICKNAME);
    public static final UserUpdateRequest userUpdateRequest = getUserUpdateRequestWithNickname(NICKNAME);

    public static final LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(USERNAME, "password");

    public static final CareerCreateRequest careerCreateRequest = getCareerCreateRequestWithJobAndCompanyName("designer", "metoridge");
    public static final EducationCreateRequest educationCreateRequest = getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(EducationLevelType.UNIVERSITY, "school", "design");
    public static final MentorSignUpRequest mentorSignUpRequest = getMentorSignUpRequestWithCareersAndEducations(Arrays.asList(careerCreateRequest), Arrays.asList(educationCreateRequest));

    public static final CareerUpdateRequest careerUpdateRequest = getCareerUpdateRequestWithJobAndCompanyName("engineer", "google");
    public static final EducationUpdateRequest educationUpdateRequest = getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajorAndOthers(EducationLevelType.UNIVERSITY, "school", "computer science", "design");
    public static final MentorUpdateRequest mentorUpdateRequest = getMentorUpdateRequestWithCareersAndEducations(Arrays.asList(careerUpdateRequest), Arrays.asList(educationUpdateRequest));
    public static final MenteeUpdateRequest menteeUpdateRequest = getMenteeUpdateRequestWithSubjects("java,spring");

    public static final LectureCreateRequest lectureCreateRequest = getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndSubjectId("제목", 1000L, 3, 10, 1L);
    public static final LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest = getLecturePriceUpdateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(2000L, 3, 5);
    public static final LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest = getLectureSubjectUpdateRequestWithSubjectId(2L);
    public static final LectureUpdateRequest lectureUpdateRequest = getLectureUpdateRequestWithLecturePricesAndLectureSubjects(Arrays.asList(lecturePriceUpdateRequest), Arrays.asList(lectureSubjectUpdateRequest));

    public static final MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(5, "좋아요");
    public static final MenteeReviewUpdateRequest menteeReviewUpdateRequest = getMenteeReviewUpdateRequestWithScoreAndContent(3, "별로에요");
    public static final MentorReviewCreateRequest mentorReviewCreateRequest = getMentorReviewCreateRequestWithContent("감사합니다");
    public static final MentorReviewUpdateRequest mentorReviewUpdateRequest = getMentorReviewUpdateRequestWithContent("리뷰 감사합니다");

    private Map<String, String> getUserInfo(String name, String username) {

        Map<String, String> userInfo = new HashMap<>();

        userInfo.put("id", "1234567890");
        userInfo.put("name", name);
        userInfo.put("email", username);
        return userInfo;
    }

    protected User menteeUser;
    protected Mentee mentee;

    protected User mentorUser;
    protected Mentor mentor;

    protected Lecture lecture1;
    protected Long lecture1Id;
    protected Lecture lecture2;
    protected Long lecture2Id;

    // @BeforeEach
    void init() {

        // subject
        subjectRepository.deleteAll();
        subjectRepository.save(getSubjectWithSubjectIdAndKrSubject(1L, "백엔드"));
        subjectRepository.save(getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드"));

        // 멘티
        menteeUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentee", "mentee"));
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        // 멘토
        mentorUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentor", "mentor"));
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture1 = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture1.approve();
        lecture1Id = lecture1.getId();

        LectureCreateRequest lectureCreateRequest2 =
                getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndSubjectId("제목2", 1000L, 3, 10, 2L);
        lecture2 = lectureService.createLecture(mentorUser, lectureCreateRequest2);
        lecture2.approve();
        lecture2Id = lecture2.getId();
    }
}
