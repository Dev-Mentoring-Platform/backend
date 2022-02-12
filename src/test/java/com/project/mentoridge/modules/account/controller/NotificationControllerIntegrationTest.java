package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.notification.vo.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class NotificationControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;

    private Lecture lecture;
    private User menteeUser;

    @BeforeEach
    void init() {

        // 튜티
        SignUpRequest signUpRequest = getSignUpRequest("mentee", "mentee");
        menteeUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());
    }

    @DisplayName("튜티가 강의 수강 시 튜터에게 알림이 오는지 확인")
    @WithAccount(NAME)
    @Test
    void getNotifications_enrollment() throws Exception {

        // Given
        // 튜터
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        lecture = lectureService.createLecture(user, lectureCreateRequest);

        // When
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());

        // Then
        mockMvc.perform(get("/users/my-notifications"))
                .andDo(print())
                .andExpect(status().isOk());
        List<Notification> notifications = notificationRepository.findByUser(user);
        notifications.stream()
                .forEach(notification -> assertFalse(notification.isChecked()));
    }

    // TODO - TEST
//    @DisplayName("채팅 메세지 도착 시 알림 오는지 확인")
//    @WithAccount(NAME)
//    @Test
//    void getNotifications_chat() {
//
//        // Given
//        // When
//        // Then
//    }

    @DisplayName("알림 확인")
    @WithAccount(NAME)
    @Test
    void getNotification() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        mockMvc.perform(put("/users/my-notifications/{notification_id}", notificationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        notification = notificationRepository.findById(notificationId).orElse(null);
        assertNotNull(notification);
        assertTrue(notification.isChecked());
    }

    @WithAccount(NAME)
    @Test
    void deleteNotification() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        mockMvc.perform(delete("/users/my-notifications/{notification_id}", notificationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertFalse(notificationRepository.findById(notificationId).isPresent());
    }

    @WithAccount(NAME)
    @Test
    void deleteNotifications() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        System.out.println(notification);

        // When
        mockMvc.perform(delete("/users/my-notifications")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("notification_ids", String.valueOf(notification.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        notifications = notificationRepository.findAllById(Arrays.asList(notification.getId()));
        assertEquals(0, notifications.size());
    }
}