package com.project.mentoridge.config.init;

import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorCancellationService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.CancellationService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.service.ReviewService;
import com.project.mentoridge.modules.review.vo.Review;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitService {

    private final LoginService loginService;
    private final MentorService mentorService;
    private final LectureService lectureService;
    private final EnrollmentService enrollmentService;
    private final CancellationService cancellationService;
    private final ReviewService reviewService;
    private final MentorCancellationService mentorCancellationService;

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;
    private final LectureSubjectRepository lectureSubjectRepository;

    private final CancellationRepository cancellationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ChatroomRepository chatroomRepository;
    private final ReviewRepository reviewRepository;

    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;

    // @PostConstruct
    @Transactional
    void init() {

        reviewRepository.deleteAll();
        chatroomRepository.deleteAll();
        cancellationRepository.deleteAll();
        enrollmentRepository.deleteAllEnrollments();
        lecturePriceRepository.deleteAll();
        lectureSubjectRepository.deleteAll();
        lectureRepository.deleteAll();
        careerRepository.deleteAll();
        educationRepository.deleteAll();
        mentorRepository.deleteAll();
        menteeRepository.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "자바"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "파이썬"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "C/C++"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "영어"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "중국어"));

        // user / mentee
        User user1 = loginService.signUp(getSignUpRequest("user1", "부산광역시 기장군 내리"));
        Mentee mentee1 = loginService.verifyEmail(user1.getUsername(), user1.getEmailVerifyToken());

        User user2 = loginService.signUp(getSignUpRequest("user2", "서울특별시 종로구 효자동"));
        Mentee mentee2 = loginService.verifyEmail(user2.getUsername(), user2.getEmailVerifyToken());

        User user3 = loginService.signUp(getSignUpRequest("user3", "경상북도 영주시 영주동"));
        Mentee mentee3 = loginService.verifyEmail(user3.getUsername(), user3.getEmailVerifyToken());

        User user4 = loginService.signUp(getSignUpRequest("user4", "부산광역시 금정구 금사동"));
        Mentee mentee4 = loginService.verifyEmail(user4.getUsername(), user4.getEmailVerifyToken());

        User user5 = loginService.signUp(getSignUpRequest("user5", "경상남도 진주시 망경동"));
        Mentee mentee5 = loginService.verifyEmail(user5.getUsername(), user5.getEmailVerifyToken());

        Mentor mentor1 = mentorService.createMentor(user4, getMentorSignUpRequest("engineer", "company1", EducationLevelType.HIGH, "school1", "computer"));
        Mentor mentor2 = mentorService.createMentor(user5, getMentorSignUpRequest("engineer", "company2", EducationLevelType.UNIVERSITY, "school2", "computer"));

        // lecture
        Lecture lecture1 = lectureService.createLecture(user4, getLectureCreateRequest("파이썬강의", 1000L, 3, 10, LearningKindType.IT, "파이썬"));
        Lecture lecture2 = lectureService.createLecture(user4, getLectureCreateRequest("자바강의", 3000L, 3, 10, LearningKindType.IT, "자바"));
        Lecture lecture3 = lectureService.createLecture(user5, getLectureCreateRequest("C/C++강의", 2000L, 5, 20, LearningKindType.IT, "C/C++"));

        // enrollment
        // chatroom
        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        LecturePrice lecturePrice2 = lecturePriceRepository.findByLecture(lecture2).get(0);
        LecturePrice lecturePrice3 = lecturePriceRepository.findByLecture(lecture3).get(0);
        Enrollment enrollment1 = enrollmentService.createEnrollment(user1, lecture1.getId(), lecturePrice1.getId());
        Enrollment enrollment2 = enrollmentService.createEnrollment(user1, lecture2.getId(), lecturePrice2.getId());
        Enrollment enrollment3 = enrollmentService.createEnrollment(user2, lecture1.getId(), lecturePrice1.getId());
        Enrollment enrollment4 = enrollmentService.createEnrollment(user2, lecture2.getId(), lecturePrice2.getId());
        Enrollment enrollment5 = enrollmentService.createEnrollment(user3, lecture3.getId(), lecturePrice3.getId());

        // 강의 종료
        // enrollmentService.close(user4, lecture1.getId(), enrollment1.getId());
        enrollmentService.close(user1, lecture1.getId());
        
        // 강의 취소 요청
        Cancellation cancellation = cancellationService.cancel(user1, lecture2.getId(), CancellationCreateRequest.of("너무 어려워요"));
        // mentorCancellationService.approve(user4, cancellation.getId());
        
        // review
        Review parent1 = reviewService.createMenteeReview(user1, lecture1.getId(), getMenteeReviewCreateRequest(5, "좋아요"));
        Review child1 = reviewService.createMentorReview(user4, lecture1.getId(), parent1.getId(), getMentorReviewCreateRequest("감사합니다!"));

        Review parent2 = reviewService.createMenteeReview(user2, lecture1.getId(), getMenteeReviewCreateRequest(3, "별로에요"));
        Review child2 = reviewService.createMentorReview(user4, lecture1.getId(), parent2.getId(), getMentorReviewCreateRequest("아쉽네요!"));

        Review parent3 = reviewService.createMenteeReview(user1, lecture2.getId(), getMenteeReviewCreateRequest(1, "환불했어요"));
        Review child3 = reviewService.createMentorReview(user4, lecture2.getId(), parent3.getId(), getMentorReviewCreateRequest("죄송합니다"));
    }

}
