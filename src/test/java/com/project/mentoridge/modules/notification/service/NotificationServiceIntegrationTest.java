package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class NotificationServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;

    @BeforeEach
    void init() {

        // subject
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("백엔드")
                    .build());
            subjectRepository.save(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("프론트엔드")
                    .build());
        }

        User user = userRepository.findAllByUsername("mentee@email.com");
        if (user != null) {
            Mentee mentee = menteeRepository.findByUser(user);
            if (mentee != null) {
                menteeRepository.delete(mentee);
            }
            userRepository.delete(user);
        }
        menteeUser = loginService.signUp(SignUpRequest.builder()
                .username("mentee@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("mentee")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("mentee")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build());
        menteeUser.verifyEmail();
        menteeRepository.save(Mentee.builder()
                .user(menteeUser)
                .build());
    }

    @WithAccount(NAME)
    @DisplayName("멘티가 강의 수강 시 멘토에게 알림이 오는지 확인")
    @Test
    void getNotifications() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        // 강의 승인
        lecture.approve();

        // When
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());

        // Then
        List<Notification> notifications = notificationRepository.findByUser(user);
        notifications.stream()
                .forEach(notification -> {
                        assertFalse(notification.isChecked());
                        assertEquals(NotificationType.ENROLLMENT, notification.getType());
                });
    }

    @WithAccount(NAME)
    @Test
    void checkAll() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        // 강의 승인
        lecture.approve();

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        notificationService.checkAll(user);

        // Then
        notification = notificationRepository.findById(notificationId).orElse(null);
        assertNotNull(notification);
        assertTrue(notification.isChecked());
    }

    @WithAccount(NAME)
    @Test
    void deleteNotification() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        // 강의 승인
        lecture.approve();

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        notificationService.deleteNotification(user, notificationId);

        // Then
        assertFalse(notificationRepository.findById(notificationId).isPresent());
    }
/*
    @WithAccount(NAME)
    @Test
    void deleteNotifications() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        // 강의 승인
        lecture.approve();

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);

        // When
        List<Long> notificationIds = Arrays.asList(notification.getId());
        notificationService.deleteNotifications(user, notificationIds);

        // Then
        notifications = notificationRepository.findAllById(notificationIds);
        assertEquals(0, notifications.size());
    }*/
}