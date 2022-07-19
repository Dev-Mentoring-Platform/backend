package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;

import java.util.ArrayList;
import java.util.List;

@MockMvcTest
public abstract class IntegrationTest {

    public static void saveAddress(AddressRepository addressRepository) {
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
    }

    public static void saveSubject(SubjectRepository subjectRepository) {
        subjectRepository.deleteAll();
        subjectRepository.save(Subject.builder()
                .subjectId(1L)
                .krSubject("프론트엔드")
                .learningKind(LearningKindType.IT)
                .build());
        subjectRepository.save(Subject.builder()
                .subjectId(2L)
                .krSubject("백엔드")
                .learningKind(LearningKindType.IT)
                .build());
    }

    public static User saveMentorUser(String name, LoginService loginService, MentorService mentorService) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name + "Name")
                .gender(GenderType.MALE)
                .birthYear("1990")
                .phoneNumber("01012345678")
                .nickname(name + "Nickname")
                .zone("서울특별시 종로구 청운동")
                .build();
        User mentorUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());

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

        return mentorUser;
    }

    public static User saveMentorUser(LoginService loginService, MentorService mentorService) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
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
        User mentorUser = loginService.signUp(signUpRequest);
        // mentorUser.generateEmailVerifyToken();   // 트랜잭션 X
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());

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

        return mentorUser;
    }

    public static User saveMenteeUser(String name, String zone, LoginService loginService) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name + "Name")
                .gender(GenderType.FEMALE)
                .birthYear("1995")
                .phoneNumber("01011112222")
                .nickname(name + "Nickname")
                .zone(zone)
                .build();
        User menteeUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        return menteeUser;
    }

    public static User saveMenteeUser(String name, LoginService loginService) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(name + "@email.com")
                .password("password")
                .passwordConfirm("password")
                .name(name + "Name")
                .gender(GenderType.FEMALE)
                .birthYear("1995")
                .phoneNumber("01011112222")
                .nickname(name + "Nickname")
                .zone("부산광역시 영도구 봉래동")
                .build();
        User menteeUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        return menteeUser;
    }

    public static User saveMenteeUser(LoginService loginService) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
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
        User menteeUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        return menteeUser;
    }

    public static Lecture saveLecture(LectureService lectureService, User mentorUser) {

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
        return lectureService.createLecture(mentorUser, lectureCreateRequest);
    }

    public static LecturePrice getLecturePrice(Lecture lecture) {
        return lecture.getLecturePrices().get(0);
    }

    public static Long savePick(PickService pickService, User menteeUser, Lecture lecture, LecturePrice lecturePrice) {
        Long lectureId = lecture.getId();
        Long lecturePriceId = lecturePrice.getId();
        return pickService.createPick(menteeUser, lectureId, lecturePriceId);
    }

    public static Enrollment saveEnrollment(EnrollmentService enrollmentService, User menteeUser, Lecture lecture, LecturePrice lecturePrice) {
        Long lectureId = lecture.getId();
        Long lecturePriceId = lecturePrice.getId();
        return enrollmentService.createEnrollment(menteeUser, lectureId, lecturePriceId);
    }

    public static MenteeReview saveMenteeReview(MenteeReviewService menteeReviewService, User menteeUser, Enrollment enrollment) {
        MenteeReviewCreateRequest createRequest = MenteeReviewCreateRequest.builder()
                .score(4)
                .content("Great!!!")
                .build();
        return menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), createRequest);
    }

    public static MentorReview saveMentorReview(MentorReviewService mentorReviewService, User mentorUser, Lecture lecture, MenteeReview menteeReview) {
        MentorReviewCreateRequest createRequest = MentorReviewCreateRequest.builder()
                .content("Thank you!!!!")
                .build();
        return mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview.getId(), createRequest);
    }
}
