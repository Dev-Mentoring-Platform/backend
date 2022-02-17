package com.project.mentoridge.configuration;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.*;
import com.project.mentoridge.modules.address.vo.Address;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.CancellationService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.*;

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
    protected CancellationService cancellationService;
    @Autowired
    protected CancellationRepository cancellationRepository;

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


    protected static final String NAME = "yk";
    protected static final String NICKNAME = NAME;
    protected static final String USERNAME = NAME + "@email.com";
    protected static final String EMAIL = USERNAME;

    public static SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname(NAME, NICKNAME);

    public Map<String, String> userInfo = getUserInfo(NAME, USERNAME);
    public static SignUpOAuthDetailRequest signUpOAuthDetailRequest = getSignUpOAuthDetailRequestWithNickname(NICKNAME);
    public static UserUpdateRequest userUpdateRequest = getUserUpdateRequestWithEmailAndNickname(EMAIL, NICKNAME);

    public static LoginRequest loginRequest = getLoginRequestWithUsernameAndPassword(USERNAME, "password");

    public static CareerCreateRequest careerCreateRequest = getCareerCreateRequestWithJobAndCompanyName("designer", "metoridge");
    public static EducationCreateRequest educationCreateRequest = getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(EducationLevelType.UNIVERSITY, "school", "design");
    public static MentorSignUpRequest mentorSignUpRequest = getMentorSignUpRequestWithCareersAndEducations(Arrays.asList(careerCreateRequest), Arrays.asList(educationCreateRequest));

    public static CareerUpdateRequest careerUpdateRequest = getCareerUpdateRequestWithJobAndCompanyName("engineer", "google");
    public static EducationUpdateRequest educationUpdateRequest = getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajorAndOthers(EducationLevelType.UNIVERSITY, "school", "computer science", "design");
    public static MentorUpdateRequest mentorUpdateRequest = getMentorUpdateRequestWithCareersAndEducations(Arrays.asList(careerUpdateRequest), Arrays.asList(educationUpdateRequest));
    public static MenteeUpdateRequest menteeUpdateRequest = getMenteeUpdateRequestWithSubjects("java,spring");

    public static LectureCreateRequest lectureCreateRequest = getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject("제목", 1000L, 3, 10, LearningKindType.IT, "자바");
    public static LectureUpdateRequest lectureUpdateRequest = getLectureUpdateRequestWithPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject(2000L, 3, 5, LearningKindType.IT, "자바스크립트");

    public static MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(5, "좋아요");
    public static MenteeReviewUpdateRequest menteeReviewUpdateRequest = getMenteeReviewUpdateRequestWithScoreAndContent(3, "별로에요");
    public static MentorReviewCreateRequest mentorReviewCreateRequest = getMentorReviewCreateRequestWithContent("감사합니다");
    public static MentorReviewUpdateRequest mentorReviewUpdateRequest = getMentorReviewUpdateRequestWithContent("리뷰 감사합니다");

    // TODO - 수정
    public static CancellationCreateRequest cancellationCreateRequest = getCancellationCreateRequestWithReason("너무 어려워요");

    private Map<String, String> getUserInfo(String name, String username) {

        Map<String, String> userInfo = new HashMap<>();

        userInfo.put("id", "1234567890");
        userInfo.put("name", name);
        userInfo.put("email", username);
        return userInfo;
    }

}
