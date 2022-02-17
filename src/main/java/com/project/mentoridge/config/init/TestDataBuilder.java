package com.project.mentoridge.config.init;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.vo.Address;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;

import java.util.Arrays;
import java.util.List;

public class TestDataBuilder {

    public static User getUserWithName(String name) {
        return User.builder()
                .username(name + "@email.com")
                .password("password")
                .name(name)
                .gender("MALE")
                .birthYear(null)
                .phoneNumber(null)
                .email(null)
                .nickname(name)
                .bio(null)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .role(RoleType.MENTEE)
                .provider(null)
                .providerId(null)
                .build();
    }

    public static Address getAddress(String state, String siGun, String gu, String dongMyunLi) {
        return Address.builder()
                .state(state)
                .siGun(siGun)
                .gu(gu)
                .dongMyunLi(dongMyunLi)
                .build();
    }

    public static SignUpRequest getSignUpRequestWithNameAndNickname(String name, String nickname) {
        return SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name)
                .gender("FEMALE")
                .birthYear(null)
                .phoneNumber(null)
                .email(null)
                .nickname(nickname)
                .bio(null)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
    }

    public static SignUpRequest getSignUpRequestWithNameAndZone(String name, String zone) {
        return SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name)
                .gender("FEMALE")
                .birthYear(null)
                .phoneNumber(null)
                .email(null)
                .nickname(name)
                .bio(null)
                .zone(zone)
                .image(null)
                .build();
    }

    public static SignUpOAuthDetailRequest getSignUpOAuthDetailRequestWithNickname(String nickname) {
        return SignUpOAuthDetailRequest.builder()
                .gender("FEMALE")
                .birthYear(null)
                .phoneNumber("010-1234-5678")
                .email(null)
                .nickname(nickname)
                .bio("hello")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
    }

    public static UserUpdateRequest getUserUpdateRequestWithEmailAndNickname(String email, String nickname) {
        return UserUpdateRequest.builder()
                .gender("FEMALE")
                .birthYear(null)
                .phoneNumber("010-1234-5678")
                .email(email)
                .nickname(nickname)
                .bio(null)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
    }

    public static MenteeUpdateRequest getMenteeUpdateRequestWithSubjects(String subjects) {
        return MenteeUpdateRequest.builder()
                .subjects(subjects)
                .build();
    }

    public static CareerCreateRequest getCareerCreateRequestWithJobAndCompanyName(String job, String companyName) {
        return CareerCreateRequest.builder()
                .job(job)
                .companyName(companyName)
                .license(null)
                .others(null)
                .build();
    }

    public static CareerUpdateRequest getCareerUpdateRequestWithJobAndCompanyName(String job, String companyName) {
        return CareerUpdateRequest.builder()
                .job(job)
                .companyName(companyName)
                .license(null)
                .others(null)
                .build();
    }

    // TODO - Enum Check
    public static EducationCreateRequest getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(EducationLevelType educationLevel, String schoolName, String major) {
        return EducationCreateRequest.builder()
                .educationLevel(educationLevel)
                .schoolName(schoolName)
                .major(major)
                .others(null)
                .build();
    }

    public static EducationUpdateRequest getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajorAndOthers(EducationLevelType educationLevel, String schoolName, String major, String others) {
        return EducationUpdateRequest.builder()
                .educationLevel(educationLevel)
                .schoolName(schoolName)
                .major(major)
                .others(others)
                .build();
    }

    public static MentorSignUpRequest getMentorSignUpRequestWithCareersAndEducations(List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        return MentorSignUpRequest.builder()
                .careers(careers)
                .educations(educations)
                .build();
    }

/*    public static MentorSignUpRequest getMentorSignUpRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajor(String job, String companyName,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major) {
        return MentorSignUpRequest.builder()
                .careers(Arrays.asList(getCareerCreateRequestWithJobAndCompanyName(job, companyName)))
                .educations(Arrays.asList(getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(educationLevel, schoolName, major)))
                .build();
    }*/

    public static MentorUpdateRequest getMentorUpdateRequestWithCareersAndEducations(List<CareerUpdateRequest> careers, List<EducationUpdateRequest> educations) {
        return MentorUpdateRequest.builder()
                .careers(careers)
                .educations(educations)
                .build();
    }

/*    public static MentorUpdateRequest getMentorUpdateRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajorAndOthers(String job, String companyName,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major, String others) {
        return MentorUpdateRequest.builder()
                .careers(Arrays.asList(getCareerUpdateRequestWithJobAndCompanyName(job, companyName)))
                .educations(Arrays.asList(getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajorAndOthers(educationLevel, schoolName, major, others)))
                .build();
    }*/

    private static LectureCreateRequest.LecturePriceCreateRequest getLecturePriceCreateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(Long pricePerHour, Integer timePerLecture, Integer numberOfLectures) {
        return LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(pricePerHour)
                .timePerLecture(timePerLecture)
                .numberOfLectures(numberOfLectures)
                .totalPrice(pricePerHour * timePerLecture * numberOfLectures)
                .build();
    }

    private static LectureCreateRequest.LectureSubjectCreateRequest getLectureSubjectCreateRequestWithLearningKindAndKrSubject(LearningKindType learningKind, String krSubject) {
        return LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .learningKind(learningKind)
                .krSubject(krSubject)
                .build();
    }

    public static LectureCreateRequest getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject(
            String title, Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, LearningKindType learningKind, String krSubject) {
        return LectureCreateRequest.builder()
                .title(title)
                .subTitle("소제목")
                .introduce("소개")
                .content("<p>본문</p>")
                .difficulty(DifficultyType.BEGINNER)
                .systems(Arrays.asList(SystemType.ONLINE))
                .lecturePrices(Arrays.asList(getLecturePriceCreateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(pricePerHour, timePerLecture, numberOfLectures)))
                .lectureSubjects(Arrays.asList(getLectureSubjectCreateRequestWithLearningKindAndKrSubject(learningKind, krSubject)))
                .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
                .build();
    }

    private static LectureUpdateRequest.LecturePriceUpdateRequest getLecturePriceUpdateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(Long pricePerHour, Integer timePerLecture, Integer numberOfLectures) {
        return LectureUpdateRequest.LecturePriceUpdateRequest.builder()
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(pricePerHour)
                .timePerLecture(timePerLecture)
                .numberOfLectures(numberOfLectures)
                .totalPrice(pricePerHour * timePerLecture * numberOfLectures)
                .build();
    }

    private static LectureUpdateRequest.LectureSubjectUpdateRequest getLectureSubjectUpdateRequestWithLearningKindAndKrSubject(LearningKindType learningKind, String krSubject) {
        return LectureUpdateRequest.LectureSubjectUpdateRequest.builder()
                .learningKind(learningKind)
                .krSubject(krSubject)
                .build();
    }

    public static LectureUpdateRequest getLectureUpdateRequestWithPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject(
            Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, LearningKindType learningKind, String krSubject) {
        return LectureUpdateRequest.builder()
                .title("제목수정")
                .subTitle("소제목수정")
                .introduce("소개수정")
                .content("<p>본문수정</p>")
                .difficulty(DifficultyType.INTERMEDIATE)
                .systems(Arrays.asList(SystemType.OFFLINE))
                .lecturePrices(Arrays.asList(getLecturePriceCreateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(pricePerHour, timePerLecture, numberOfLectures)))
                .lectureSubjects(Arrays.asList(getLectureSubjectCreateRequestWithLearningKindAndKrSubject(learningKind, krSubject)))
                .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
                .build();
    }

    public static MenteeReviewCreateRequest getMenteeReviewCreateRequestWithScoreAndContent(Integer score, String content) {
        return MenteeReviewCreateRequest.builder()
                .score(score)
                .content(content)
                .build();
    }

    public static MenteeReviewUpdateRequest getMenteeReviewUpdateRequestWithScoreAndContent(Integer score, String content) {
        return MenteeReviewUpdateRequest.builder()
                .score(score)
                .content(content)
                .build();
    }

    public static MentorReviewCreateRequest getMentorReviewCreateRequestWithContent(String content) {
        return MentorReviewCreateRequest.builder()
                .content(content)
                .build();
    }

    public static MentorReviewUpdateRequest getMentorReviewUpdateRequestWithContent(String content) {
        return MentorReviewUpdateRequest.builder()
                .content(content)
                .build();
    }

    public static LoginRequest getLoginRequestWithUsernameAndPassword(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    public static CancellationCreateRequest getCancellationCreateRequestWithReason(String reason) {
        return CancellationCreateRequest.builder()
                .reason(reason)
                .build();
    }
}
