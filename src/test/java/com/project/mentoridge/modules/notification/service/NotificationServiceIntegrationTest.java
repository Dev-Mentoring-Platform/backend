package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.vo.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class NotificationServiceIntegrationTest extends AbstractTest {

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
    void check() {

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
        // notificationService.check(user, notificationId);
        fail();

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