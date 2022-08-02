package com.project.mentoridge.modules.base;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.vo.Address;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.subject.vo.Subject;
import com.project.mentoridge.modules.upload.service.request.FileRequest;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.modules.upload.enums.FileType.LECTURE_IMAGE;

public class TestDataBuilder {

    public static User getUserWithName(String name) {
        return User.builder()
                .username(name + "@email.com")
                .password("password")
                .name(name)
                .gender(GenderType.MALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(name)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .role(RoleType.MENTEE)
                .provider(null)
                .providerId(null)
                .build();
    }

    public static User getUserWithNameAndRole(String name, RoleType role) {
        return User.builder()
                .username(name + "@email.com")
                .password("password")
                .name(name)
                .gender(GenderType.MALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(name)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .role(role)
                .provider(null)
                .providerId(null)
                .build();
    }

    public static Career getCareerWithMentor(Mentor mentor) {
        return Career.builder()
                .mentor(mentor)
                .job("job")
                .companyName("companyName")
                .license("license")
                .others("others")
                .build();
    }

    public static Education getEducationWithMentor(Mentor mentor) {
        return Education.builder()
                .mentor(mentor)
                .educationLevel(EducationLevelType.MIDDLE)
                .schoolName("schoolName")
                .major("major")
                .others("others")
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

    public static Subject getSubjectWithSubjectIdAndKrSubject(Long subjectId, String krSubject) {
        return Subject.builder()
                .subjectId(subjectId)
                .learningKind(LearningKindType.IT)
                .krSubject(krSubject)
                .build();
    }

    public static SignUpRequest getSignUpRequestWithNameAndNickname(String name, String nickname) {
        return SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name)
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(nickname)
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
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(name)
                .zone(zone)
                .image(null)
                .build();
    }

    public static SignUpOAuthDetailRequest getSignUpOAuthDetailRequestWithNickname(String nickname) {
        return SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("01012345678")
                .nickname(nickname)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
    }

    public static UserUpdateRequest getUserUpdateRequestWithNickname(String nickname) {
        return UserUpdateRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("01012345678")
                .nickname(nickname)
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
    }

    public static UserImageUpdateRequest getUserImageUpdateRequestWithImage(String image) {
        return UserImageUpdateRequest.builder()
                .image(image)
                .build();
    }

    public static UserQuitRequest getUserQuitRequestWithReasonIdAndPassword(Integer reasonId, String password) {
        return UserQuitRequest.builder()
                .reasonId(reasonId)
                //.reason(UserQuitRequest.reasons.get(reasonId))
                .password("password")
                .build();
    }

    public static UserQuitRequest getUserQuitRequestWithReasonIdAndReasonAndPassword(Integer reasonId, String reason, String password) {
        return UserQuitRequest.builder()
                .reasonId(reasonId)
                .reason(reason)
                .password("password")
                .build();
    }

    public static UserPasswordUpdateRequest getUserPasswordUpdateRequestWithPasswordAndNewPasswordAndNewPasswordConfirm(String password, String newPassword, String newPasswordConfirm) {
        return UserPasswordUpdateRequest.builder()
                .password(password)
                .newPassword(newPassword)
                .newPasswordConfirm(newPasswordConfirm)
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

    public static CareerCreateRequest getCareerCreateRequestWithJobAndCompanyNameAndLicenseAndOthers(String job, String companyName, String license, String others) {
        return CareerCreateRequest.builder()
                .job(job)
                .companyName(companyName)
                .license(license)
                .others(others)
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

    public static CareerUpdateRequest getCareerUpdateRequestWithJobAndCompanyNameAndLicenseAndOthers(String job, String companyName, String license, String others) {
        return CareerUpdateRequest.builder()
                .job(job)
                .companyName(companyName)
                .license(license)
                .others(others)
                .build();
    }

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
                .bio("hello")
                .careers(careers)
                .educations(educations)
                .build();
    }

    public static MentorSignUpRequest getMentorSignUpRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajor(String job, String companyName,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major) {
        return MentorSignUpRequest.builder()
                .bio("hello")
                .careers(Arrays.asList(getCareerCreateRequestWithJobAndCompanyName(job, companyName)))
                .educations(Arrays.asList(getEducationCreateRequestWithEducationLevelAndSchoolNameAndMajor(educationLevel, schoolName, major)))
                .build();
    }

    public static MentorUpdateRequest getMentorUpdateRequestWithCareersAndEducations(List<CareerUpdateRequest> careers, List<EducationUpdateRequest> educations) {
        return MentorUpdateRequest.builder()
                .bio("hi~")
                .careers(careers)
                .educations(educations)
                .build();
    }

/*    public static MentorUpdateRequest getMentorUpdateRequestWithJobAndCompanyNameAndEducationLevelAndSchoolNameAndMajorAndOthers(String job, String companyName,
                                                                                                                        EducationLevelType educationLevel, String schoolName, String major, String others) {
        return MentorUpdateRequest.builder()
                .bio("hi~")
                .careers(Arrays.asList(getCareerUpdateRequestWithJobAndCompanyName(job, companyName)))
                .educations(Arrays.asList(getEducationUpdateRequestWithEducationLevelAndSchoolNameAndMajorAndOthers(educationLevel, schoolName, major, others)))
                .build();
    }*/

    public static LectureCreateRequest.LecturePriceCreateRequest getLecturePriceCreateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(Long pricePerHour, Integer timePerLecture, Integer numberOfLectures) {
        return LectureCreateRequest.LecturePriceCreateRequest.builder()
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(pricePerHour)
                .timePerLecture(timePerLecture)
                .numberOfLectures(numberOfLectures)
                .totalPrice(pricePerHour * timePerLecture * numberOfLectures)
                .build();
    }

    public static LectureCreateRequest.LectureSubjectCreateRequest getLectureSubjectCreateRequestWithSubjectId(Long subjectId) {
        return LectureCreateRequest.LectureSubjectCreateRequest.builder()
                .subjectId(subjectId)
                .build();
    }

    public static LectureCreateRequest getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndSubjectId(
            String title, Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, Long subjectId) {
        return LectureCreateRequest.builder()
                .title(title)
                .subTitle("소제목")
                .introduce("소개")
                .content("<p>본문</p>")
                .difficulty(DifficultyType.BEGINNER)
                .systems(Arrays.asList(SystemType.ONLINE))
                .lecturePrices(Arrays.asList(getLecturePriceCreateRequestWithPricePerHourAndTimePerLectureAndNumberOfLectures(pricePerHour, timePerLecture, numberOfLectures)))
                .lectureSubjects(Arrays.asList(getLectureSubjectCreateRequestWithSubjectId(subjectId)))
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

    public static InquiryCreateRequest getInquiryCreateRequestWithInquiryType(InquiryType type) {
        return InquiryCreateRequest.builder()
                .type(type)
                .title("title")
                .content("content")
                .build();
    }

    public static FileRequest getFileRequest() {
        return FileRequest.builder()
                .uuid("uuid")
                .name("test.jpg")
                .contentType("image/jpg")
                .type(LECTURE_IMAGE)
                .size(2424L)
                .build();
    }
}
