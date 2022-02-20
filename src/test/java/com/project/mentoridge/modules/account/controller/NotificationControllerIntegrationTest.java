package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.notification.vo.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class NotificationControllerIntegrationTest extends AbstractTest {

    private final static String BASE_URL = "/api/users/my-notifications";

    @Autowired
    MockMvc mockMvc;

    @DisplayName("멘티가 강의 수강 시 멘토에게 알림이 오는지 확인")
    @WithAccount(NAME)
    @Test
    void getNotifications_enrollment() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        // When
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());

        // Then
        mockMvc.perform(get(BASE_URL))
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
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        mockMvc.perform(put(BASE_URL + "/{notification_id}", notificationId))
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
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        mockMvc.perform(delete(BASE_URL + "/{notification_id}", notificationId))
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
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        System.out.println(notification);

        // When
        mockMvc.perform(delete(BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("notification_ids", String.valueOf(notification.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        notifications = notificationRepository.findAllById(Arrays.asList(notification.getId()));
        assertEquals(0, notifications.size());
    }
}