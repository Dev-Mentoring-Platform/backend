package com.project.mentoridge.config.init;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
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

    public static CareerUpdateRequest getCareerUpdateRequestWithJobAndCompanyNameAndLicense(String job, String companyName, String license) {
        return CareerUpdateRequest.builder()
                .job(job)
                .companyName(companyName)
                .license(license)
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

    public static EducationUpdateRequest getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajor(EducationLevelType educationLevel, String schoolName, String major) {
        return EducationUpdateRequest.builder()
                .educationLevel(educationLevel)
                .schoolName(schoolName)
                .major(major)
                .others(null)
                .build();
    }

    public static MentorSignUpRequest getMentorSignUpRequestWithCareersAndEducations(List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        return MentorSignUpRequest.builder()
                .careers(careers)
                .educations(educations)
                .build();
    }

    public static MentorSignUpRequest getMentorSignUpRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajor(String job, String companyName,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major) {
        return MentorSignUpRequest.builder()
                .careers(Arrays.asList(getCareerCreateRequestWithJobAndCompanyName(job, companyName)))
                .educations(Arrays.asList(getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(educationLevel, schoolName, major)))
                .build();
    }

    public static MentorUpdateRequest getMentorUpdateRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajor(String job, String companyName, String license,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major) {
        return MentorUpdateRequest.builder()
                .careers(Arrays.asList(getCareerUpdateRequestWithJobAndCompanyNameAndLicense(job, companyName, license)))
                .educations(Arrays.asList(getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajor(educationLevel, schoolName, major)))
                .build();
    }

    public static LectureCreateRequest.LecturePriceCreateRequest getLecturePriceCreateRequest(Long pertimeCost, Integer pertimeLecture, Integer totalTime) {
        return LectureCreateRequest.LecturePriceCreateRequest.of(
                true,
                3,
                pertimeCost,
                pertimeLecture,
                totalTime,
                pertimeCost * pertimeLecture * totalTime
        );
    }

    public static LectureCreateRequest.LectureSubjectCreateRequest getLectureSubjectCreateRequestWithLearningKindAndKrSubject(LearningKindType learningKind, String krSubject) {
        return LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .learningKind(learningKind)
                .krSubject(krSubject)
                .build();
    }

    public static LectureCreateRequest getLectureCreateRequest(String title, Long pertimeCost, Integer pertimeLecture, Integer totalTime, LearningKindType learningKind, String krSubject) {
        return LectureCreateRequest.builder()
                .thumbnailUrl("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
                .title(title)
                .subTitle("소제목")
                .introduce("소개")
                .difficulty(DifficultyType.BEGINNER)
                .content("<p>본문</p>")
                .systems(Arrays.asList(SystemType.ONLINE))
                .lecturePrices(Arrays.asList(getLecturePriceCreateRequest(pertimeCost, pertimeLecture, totalTime)))
                .subjects(Arrays.asList(getLectureSubjectCreateRequestWithLearningKindAndKrSubject(learningKind, krSubject)))
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
}
