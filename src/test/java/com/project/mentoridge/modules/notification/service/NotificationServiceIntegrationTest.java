package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.NotificationResponse;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class NotificationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;

    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture;
    private LecturePrice lecturePrice;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
    }

    @Test
    void get_paged_NotificationResponses() {

        // Given
        Notification notification1 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification2 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification3 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.CHAT)
                .build());
        Notification notification4 = notificationRepository.save(Notification.builder()
                .user(menteeUser)
                .type(NotificationType.CHAT)
                .build());
        notification4.check();

        // When
        Page<NotificationResponse> notificationResponsesOfMentorUser = notificationService.getNotificationResponses(mentorUser, 1);
        Page<NotificationResponse> notificationResponsesOfMenteeUser = notificationService.getNotificationResponses(menteeUser, 1);
        // Then
        assertThat(notificationResponsesOfMentorUser.getTotalElements()).isEqualTo(3L);
        assertThat(notificationResponsesOfMenteeUser.getTotalElements()).isEqualTo(1L);
        NotificationResponse notificationResponse = notificationResponsesOfMenteeUser.getContent().get(0);
        assertAll(
                () -> assertThat(notificationResponse.getNotificationId()).isEqualTo(notification4.getId()),
                () -> assertThat(notificationResponse.getType()).isEqualTo(notification4.getType()),
                () -> assertThat(notificationResponse.getContent()).isEqualTo(notification4.getContent()),
                () -> assertThat(notificationResponse.getCreatedAt()).isNotNull(),
                () -> assertThat(notificationResponse.isChecked()).isTrue(),
                () -> assertThat(notificationResponse.getCheckedAt()).isNotNull()
        );

    }

    @DisplayName("멘티가 강의 수강 시 멘토에게 알림이 오는지 확인")
    @Test
    void check_notification_of_mentor_when_mentee_enroll_lecture() {

        // Given
        // When
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // Then
        List<Notification> notifications = notificationRepository.findByUser(mentorUser);
        notifications
                .forEach(notification -> {
                        assertFalse(notification.isChecked());
                        assertEquals(NotificationType.ENROLLMENT, notification.getType());
                });
    }

    @Test
    void count_unchecked_notifications() {

        // Given
        Notification notification1 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        notification1.check();
        Notification notification2 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification3 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.CHAT)
                .build());

        // When
        int count = notificationService.countUncheckedNotifications(mentorUser);
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void createNotification() {

        // Given
        // When
        Notification notification = notificationService.createNotification(menteeUser, NotificationType.CHAT);
        // Then
        assertTrue(notificationRepository.findById(notification.getId()).isPresent());
        // verify - messageSendingTemplate
    }

    @Test
    void checkAll() {

        // Given
        Notification notification1 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification2 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification3 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.CHAT)
                .build());

        // When
        notificationService.checkAll(mentorUser);

        // Then
        assertTrue(notification1.isChecked());
        assertNotNull(notification1.getCheckedAt());
        assertTrue(notification2.isChecked());
        assertNotNull(notification2.getCheckedAt());
        assertTrue(notification3.isChecked());
        assertNotNull(notification3.getCheckedAt());
    }

    @Test
    void deleteNotification() {

        // Given
        Notification notification1 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification2 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.ENROLLMENT)
                .build());
        Notification notification3 = notificationRepository.save(Notification.builder()
                .user(mentorUser)
                .type(NotificationType.CHAT)
                .build());

        // When
        notificationService.deleteNotification(mentorUser, notification2.getId());
        // Then
        assertFalse(notificationRepository.findById(notification2.getId()).isPresent());
    }
/*
    @WithAccount(NAME)
    @Test
    void deleteNotifications() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
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