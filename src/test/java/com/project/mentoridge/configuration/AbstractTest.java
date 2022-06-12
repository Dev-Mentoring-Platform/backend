package com.project.mentoridge.configuration;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static com.project.mentoridge.config.init.TestDataBuilder.getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndSubjectId;
import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;

public abstract class AbstractTest {

//    @Autowired
//    private FilterChainProxy springSecurityFilterChain;

//    MockMvc mockMvc;
//
//    @BeforeEach
//    private void setUp(WebApplicationContext webAppContext) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .build();
//    }

    static final String NAME = "user";
    static final String NICKNAME = NAME;
    static final String USERNAME = NAME + "@email.com";

    public static SignUpRequest signUpRequest = SignUpRequest.builder()
            .username(USERNAME)
            .password("password")
            .passwordConfirm("password")
            .name(NAME)
            .gender(GenderType.FEMALE)
            .birthYear(null)
            .phoneNumber(null)
            .nickname(NICKNAME)
            .zone("서울특별시 강남구 삼성동")
            .image(null)
            .build();

    public static final SignUpOAuthDetailRequest signUpOAuthDetailRequest = SignUpOAuthDetailRequest.builder()
            .gender(GenderType.FEMALE)
            .birthYear(null)
            .phoneNumber("01012345678")
            .nickname(NICKNAME)
            .zone("서울특별시 강남구 삼성동")
            .image(null)
            .build();

    public static final UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .gender(GenderType.FEMALE)
            .birthYear(null)
            .phoneNumber("01012345678")
            .nickname(NICKNAME)
            .zone("서울특별시 강남구 삼성동")
            .image(null)
            .build();

    public static final LoginRequest loginRequest = LoginRequest.builder()
            .username(USERNAME)
            .password("password")
            .build();

    public static final CareerCreateRequest careerCreateRequest = CareerCreateRequest.builder()
            .job("designer")
            .companyName("metoridge")
            .license(null)
            .others(null)
            .build();

    public static final EducationCreateRequest educationCreateRequest = EducationCreateRequest.builder()
            .educationLevel(EducationLevelType.UNIVERSITY)
            .schoolName("school")
            .major("design")
            .others(null)
            .build();

    public static final MentorSignUpRequest mentorSignUpRequest = MentorSignUpRequest.builder()
            .bio("hello")
            .careers(Arrays.asList(careerCreateRequest))
            .educations(Arrays.asList(educationCreateRequest))
            .build();

    public static final CareerUpdateRequest careerUpdateRequest = CareerUpdateRequest.builder()
            .job("engineer")
            .companyName("google")
            .license(null)
            .others(null)
            .build();

    public static final EducationUpdateRequest educationUpdateRequest = EducationUpdateRequest.builder()
            .educationLevel(EducationLevelType.UNIVERSITY)
            .schoolName("school")
            .major("computer science")
            .others("design")
            .build();

    public static final MentorUpdateRequest mentorUpdateRequest = MentorUpdateRequest.builder()
            .bio("hi~")
            .careers(Arrays.asList(careerUpdateRequest))
            .educations(Arrays.asList(educationUpdateRequest))
            .build();

    public static final MenteeUpdateRequest menteeUpdateRequest = MenteeUpdateRequest.builder()
            .subjects("java,spring")
            .build();

    public static final LectureCreateRequest lectureCreateRequest = LectureCreateRequest.builder()
            .title("제목")
            .subTitle("소제목")
            .introduce("소개")
            .content("<p>본문</p>")
            .difficulty(DifficultyType.BEGINNER)
            .systems(Arrays.asList(SystemType.ONLINE))
            .lecturePrices(Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.builder()
                    .isGroup(true)
                    .numberOfMembers(10)
                    .pricePerHour(1000L)
                    .timePerLecture(3)
                    .numberOfLectures(10)
                    .totalPrice(1000L * 3 * 10)
                    .build()))
            .lectureSubjects(Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                    .subjectId(1L)
                    .build()))
            .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
            .build();

    public static final LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest = LectureUpdateRequest.LecturePriceUpdateRequest.builder()
            .isGroup(true)
            .numberOfMembers(10)
            .pricePerHour(2000L)
            .timePerLecture(3)
            .numberOfLectures(5)
            .totalPrice(2000L * 3 * 5)
            .build();

    public static final LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest = LectureUpdateRequest.LectureSubjectUpdateRequest.builder()
            .subjectId(2L)
            .build();

    public static final LectureUpdateRequest lectureUpdateRequest = LectureUpdateRequest.builder()
            .title("제목수정")
            .subTitle("소제목수정")
            .introduce("소개수정")
            .content("<p>본문수정</p>")
            .difficulty(DifficultyType.INTERMEDIATE)
            .systems(Arrays.asList(SystemType.OFFLINE))
            .lecturePrices(Arrays.asList(lecturePriceUpdateRequest))
            .lectureSubjects(Arrays.asList(lectureSubjectUpdateRequest))
            .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
            .build();

    public static final MenteeReviewCreateRequest menteeReviewCreateRequest = MenteeReviewCreateRequest.builder()
            .score(5)
            .content("좋아요")
            .build();

    public static final MenteeReviewUpdateRequest menteeReviewUpdateRequest = MenteeReviewUpdateRequest.builder()
            .score(3)
            .content("별로에요")
            .build();

    public static final MentorReviewCreateRequest mentorReviewCreateRequest = MentorReviewCreateRequest.builder()
            .content("감사합니다")
            .build();

    public static final MentorReviewUpdateRequest mentorReviewUpdateRequest = MentorReviewUpdateRequest.builder()
            .content("리뷰 감사합니다")
            .build();

}
