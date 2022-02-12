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

    public static User getUser(String name) {
        return User.of(
                name + "@email.com",
                "password",
                name,
                "MALE",
                null,
                null,
                null,
                name,
                null,
                "서울특별시 강남구 삼성동",
                null,
                RoleType.MENTEE,
                null,
                null
        );
    }

    public static SignUpRequest getSignUpRequest(String name, String zone) {
        return SignUpRequest.of(
                name + "@email.com",
                "password",
                "password",
                name,
                "FEMALE",
                null,
                null,
                null,
                name,
                null,
                zone,
                null
        );
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

    public static MenteeUpdateRequest getMenteeUpdateRequest(String subjects) {
        return MenteeUpdateRequest.of(subjects);
    }

    public static CareerCreateRequest getCareerCreateRequest(String job, String companyName) {
        return CareerCreateRequest.of(
                job,
                companyName,
                null,
                null
        );
    }

    public static CareerUpdateRequest getCareerUpdateRequest(String job, String companyName, String others, String license) {
        return CareerUpdateRequest.of(
                job,
                companyName,
                others,
                license
        );
    }

    // TODO - Enum Check
    public static EducationCreateRequest getEducationCreateRequest(EducationLevelType educationLevel, String schoolName, String major) {
        return EducationCreateRequest.of(
                educationLevel,
                schoolName,
                major,
                null
        );
    }

    public static EducationUpdateRequest getEducationUpdateRequest(EducationLevelType educationLevel, String schoolName, String major, String others) {
        return EducationUpdateRequest.of(
                educationLevel,
                schoolName,
                major,
                others
        );
    }

    public static MentorSignUpRequest getMentorSignUpRequest(List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        return MentorSignUpRequest.of(
                careers,
                educations
        );
    }

    public static MentorSignUpRequest getMentorSignUpRequest(String job, String companyName,
                                                           EducationLevelType educationLevel, String schoolName, String major) {
        return MentorSignUpRequest.of(
                Arrays.asList(getCareerCreateRequest(job, companyName)),
                Arrays.asList(getEducationCreateRequest(educationLevel, schoolName, major))
        );
    }

    public static MentorUpdateRequest getMentorUpdateRequest(String job, String companyName, String otherCareers, String license,
                                                           EducationLevelType educationLevel, String schoolName, String major, String otherEducations) {
        return MentorUpdateRequest.of(
                Arrays.asList(getCareerUpdateRequest(job, companyName, otherCareers, license)),
                Arrays.asList(getEducationUpdateRequest(educationLevel, schoolName, major, otherEducations))
        );
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

    public static LectureCreateRequest.LectureSubjectCreateRequest getLectureSubjectCreateRequest(LearningKindType type, String krSubject) {
        return LectureCreateRequest.LectureSubjectCreateRequest.of(type, krSubject);
    }

    public static LectureCreateRequest getLectureCreateRequest(String title, Long pertimeCost, Integer pertimeLecture, Integer totalTime, LearningKindType type, String krSubject) {

        LectureCreateRequest.LecturePriceCreateRequest price = getLecturePriceCreateRequest(pertimeCost, pertimeLecture, totalTime);
        LectureCreateRequest.LectureSubjectCreateRequest subject = getLectureSubjectCreateRequest(type, krSubject);

        return LectureCreateRequest.of(
                "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
                title,
                "소제목",
                "소개",
                DifficultyType.BEGINNER,
                "<p>본문</p>",
                Arrays.asList(SystemType.ONLINE),
                Arrays.asList(price),
                Arrays.asList(subject)
        );
    }

    public static MenteeReviewCreateRequest getMenteeReviewCreateRequest(Integer score, String content) {
        return MenteeReviewCreateRequest.of(score, content);
    }

    public static MenteeReviewUpdateRequest getMenteeReviewUpdateRequest(Integer score, String content) {
        return MenteeReviewUpdateRequest.of(score, content);
    }

    public static MentorReviewCreateRequest getMentorReviewCreateRequest(String content) {
        return MentorReviewCreateRequest.of(content);
    }

    public static MentorReviewUpdateRequest getMentorReviewUpdateRequest(String content) {
        return MentorReviewUpdateRequest.of(content);
    }

    public static LoginRequest getLoginRequest(String username, String password) {
        return LoginRequest.of(username, password);
    }
}
