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

// @ExtendWith({SpringExtension.class})
// @SpringBootTest(classes = mentoridgeApplication.class)
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

    protected final SignUpRequest signUpRequest = getSignUpRequest(NAME, NICKNAME);
    protected final UserUpdateRequest userUpdateRequest = getUserUpdateRequest(EMAIL, NICKNAME);

    protected MentorSignUpRequest mentorSignUpRequest = getMentorSignUpRequest();
    protected final CareerCreateRequest careerCreateRequest = getCareerCreateRequest();
    protected final CareerUpdateRequest careerUpdateRequest = getCareerUpdateRequest();
    protected final EducationCreateRequest educationCreateRequest = getEducationCreateRequest();
    protected final EducationUpdateRequest educationUpdateRequest = getEducationUpdateRequest();

    protected final MentorUpdateRequest mentorUpdateRequest = getMentorUpdateRequest();
    protected final MenteeUpdateRequest menteeUpdateRequest = getMenteeUpdateRequest();

    protected final LoginRequest loginRequest = getLoginRequest(USERNAME);

    protected final Map<String, String> userInfo = getUserInfo(NAME, USERNAME);
    protected final SignUpOAuthDetailRequest signUpOAuthDetailRequest = getSignUpOAuthDetailRequest(NICKNAME);

    protected final LectureCreateRequest lectureCreateRequest = getLectureCreateRequest();
    protected final LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest = getLecturePriceUpdateRequest();
    protected final LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest = getLectureSubjectUpdateRequest();
    protected final LectureUpdateRequest lectureUpdateRequest = getLectureUpdateRequest();

    protected final MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequest();
    protected final MenteeReviewUpdateRequest menteeReviewUpdateRequest = getMenteeReviewUpdateRequest();

    protected final MentorReviewCreateRequest mentorReviewCreateRequest = getMentorReviewCreateRequest();
    protected final MentorReviewUpdateRequest mentorReviewUpdateRequest = getMentorReviewUpdateRequest();

    // TODO - 수정
    protected final CancellationCreateRequest cancellationCreateRequest = getCancellationCreateRequest();

    public static CancellationCreateRequest getCancellationCreateRequest() {
        return CancellationCreateRequest.of("너무 어려워요");
    }

//    LectureCreateRequest lectureCreateRequest1 = LectureCreateRequest.of(
//            "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
//            "제목1",
//            "소제목1",
//            "소개1",
//            DifficultyType.BASIC,
//            "<p>본문1</p>",
//            Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE),
//            Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.of(
//                    false, null, 1000L, 1, 10, 10000L
//            )),
//            Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.of(
//                    LearningKindType.IT, "자바")
//            )
//    );
//
//    LectureCreateRequest lectureCreateRequest2 = LectureCreateRequest.of(
//            "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
//            "제목2",
//            "소제목2",
//            "소개2",
//            DifficultyType.BEGINNER,
//            "<p>본문2</p>",
//            Arrays.asList(SystemType.ONLINE),
//            Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.of(
//                    true, 5, 1000L, 2, 10, 20000L
//            )),
//            Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.of(
//                    LearningKindType.IT, "파이썬")
//            )
//    );
//
//    LectureCreateRequest lectureCreateRequest3 = LectureCreateRequest.of(
//            "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
//            "제목3",
//            "소제목3",
//            "소개3",
//            DifficultyType.INTERMEDIATE,
//            "<p>본문3</p>",
//            Arrays.asList(SystemType.OFFLINE),
//            Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.of(
//                    true, 10, 1000L, 3, 10, 30000L
//            )),
//            Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.of(
//                    LearningKindType.IT, "자바")
//            )
//    );
//
//    LectureCreateRequest lectureCreateRequest4 = LectureCreateRequest.of(
//            "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
//            "제목4",
//            "소제목4",
//            "소개4",
//            DifficultyType.ADVANCED,
//            "<p>본문4</p>",
//            Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE),
//            Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.of(
//                    false, null, 1000L, 4, 10, 40000L
//            )),
//            Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.of(
//                    LearningKindType.IT, "파이썬")
//            )
//    );


    protected Address getAddress(String state, String siGun, String gu, String dongMyunLi) {
        return Address.of(state, siGun, gu, dongMyunLi);
    }

    public static UserUpdateRequest getUserUpdateRequest(String email, String nickname) {
        return UserUpdateRequest.of(
                "FEMALE",
                null,
                "010-1234-5678",
                email,
                nickname,
                null,
                "서울특별시 강남구 삼성동",
                null
        );
    }

    public static MentorUpdateRequest getMentorUpdateRequest() {
        return MentorUpdateRequest.of(
                Arrays.asList(getCareerUpdateRequest()),
                Arrays.asList(getEducationUpdateRequest())
        );
    }

    public static MentorSignUpRequest getMentorSignUpRequest() {
        return MentorSignUpRequest.of(
                Arrays.asList(getCareerCreateRequest()),
                Arrays.asList(getEducationCreateRequest())
        );
    }

    public static MenteeUpdateRequest getMenteeUpdateRequest() {
        return MenteeUpdateRequest.of("java,spring");
    }

    public static SignUpOAuthDetailRequest getSignUpOAuthDetailRequest(String nickname) {
        return SignUpOAuthDetailRequest.of(
                "FEMALE",
                null,
                "010-1234-5678",
                null,
                nickname,
                "hello",
                "서울특별시 강남구 삼성동",
                null
        );
    }

    private Map<String, String> getUserInfo(String name, String username) {

        Map<String, String> userInfo = new HashMap<>();

        userInfo.put("id", "1234567890");
        userInfo.put("name", name);
        userInfo.put("email", username);
        return userInfo;
    }

    public static SignUpRequest getSignUpRequest(String name, String nickname) {
        return SignUpRequest.of(
                name + "@email.com",
                "password",
                "password",
                name,
                "FEMALE",
                null,
                null,
                null,
                nickname,
                null,
                "서울특별시 강남구 삼성동",
                null
        );
    }

    public static CareerCreateRequest getCareerCreateRequest() {
        return CareerCreateRequest.of(
                "engineer",
                "mentoridge",
                "",
                ""
        );
    }

    public static CareerUpdateRequest getCareerUpdateRequest() {
        return CareerUpdateRequest.of(
                "engineer",
                "mentoridge2",
                "designer",
                "computer"
        );
    }

    public static EducationCreateRequest getEducationCreateRequest() {
        return EducationCreateRequest.of(
                EducationLevelType.UNIVERSITY,
                "school",
                "computer",
                ""
        );
    }

    public static EducationUpdateRequest getEducationUpdateRequest() {
        return EducationUpdateRequest.of(
                EducationLevelType.UNIVERSITY,
                "school",
                "computer science",
                "design"
        );
    }

    private LoginRequest getLoginRequest(String username) {
        return LoginRequest.of(
                username, "password"
        );
    }

    public static LectureCreateRequest.LecturePriceCreateRequest getLecturePriceCreateRequest() {
        return LectureCreateRequest.LecturePriceCreateRequest.of(
                true, 3, 1000L, 3, 10, 3000L
        );
    }

    public static LectureCreateRequest.LectureSubjectCreateRequest getLectureSubjectCreateRequest() {
        return LectureCreateRequest.LectureSubjectCreateRequest.of(LearningKindType.IT, "자바");
    }

    public static LectureCreateRequest getLectureCreateRequest() {
        return LectureCreateRequest.of(
                "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
                "제목",
                "소제목",
                "소개",
                DifficultyType.BEGINNER,
                "<p>본문</p>",
                Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE),
                Arrays.asList(getLecturePriceCreateRequest()),
                Arrays.asList(getLectureSubjectCreateRequest())
        );
    }

    public static LectureUpdateRequest.LecturePriceUpdateRequest getLecturePriceUpdateRequest() {
        return LectureUpdateRequest.LecturePriceUpdateRequest.of(
                false, 3, 1000L, 3, 10, 30000L
        );
    }

    public static LectureUpdateRequest.LectureSubjectUpdateRequest getLectureSubjectUpdateRequest() {
        return LectureUpdateRequest.LectureSubjectUpdateRequest.of(LearningKindType.IT, "자바스크립트");
    }

    public static LectureUpdateRequest getLectureUpdateRequest() {
        return LectureUpdateRequest.of(
                "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
                "제목수정",
                "소제목수정",
                "소개수정",
                DifficultyType.INTERMEDIATE,
                "<p>본문수정</p>",
                Arrays.asList(SystemType.OFFLINE),
                Arrays.asList(getLecturePriceUpdateRequest()),
                Arrays.asList(getLectureSubjectUpdateRequest())
        );
    }

    public static MenteeReviewCreateRequest getMenteeReviewCreateRequest() {
        return MenteeReviewCreateRequest.of(
                5, "좋아요"
        );
    }

    public static MenteeReviewUpdateRequest getMenteeReviewUpdateRequest() {
        return MenteeReviewUpdateRequest.of(
                3, "별로에요"
        );
    }

    public static MentorReviewCreateRequest getMentorReviewCreateRequest() {
        return MentorReviewCreateRequest.of("감사합니다");
    }

    public static MentorReviewUpdateRequest getMentorReviewUpdateRequest() {
        return MentorReviewUpdateRequest.of("리뷰 감사합니다");
    }
}
