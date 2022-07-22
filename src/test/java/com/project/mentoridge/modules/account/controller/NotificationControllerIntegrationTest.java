package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class NotificationControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/users/my-notifications";

    @Autowired
    MockMvc mockMvc;

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
    LectureLogService lectureLogService;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private String menteeAccessToken;

    private User mentorUser;
    private String mentorAccessToken;
    private Lecture lecture;
    private LecturePrice lecturePrice;

    @BeforeEach
    @Override
    protected void init() {

        // saveAddress(addressRepository);
        saveSubject(subjectRepository);

        menteeUser = saveMenteeUser(loginService);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);
    }

    @DisplayName("멘티가 강의 수강 시 멘토에게 알림이 오는지 확인")
    @Test
    void get_notifications_when_mentee_enrolled_lecture() throws Exception {

        // Given
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$..notificationId").exists())
                .andExpect(jsonPath("$..type").value(NotificationType.ENROLLMENT))
                .andExpect(jsonPath("$..content").value(NotificationType.ENROLLMENT.getMessage()))
                .andExpect(jsonPath("$..checked").value(false))
                .andExpect(jsonPath("$..createdAt").exists())
                .andExpect(jsonPath("$..checkedAt").exists());

        // Then
        List<Notification> notifications = notificationRepository.findByUser(mentorUser);
        assertThat(notifications.size()).isGreaterThan(1);
        notifications.stream()
                .forEach(notification -> assertFalse(notification.isChecked()));
    }

    // TODO - CHECK
    @DisplayName("알림 확인")
    @Test
    void check_all_notifications() throws Exception {

        // Given
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        mockMvc.perform(put(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        List<Notification> notifications = notificationRepository.findByUser(mentorUser);
        assertThat(notifications.size()).isGreaterThan(1);
        notifications.stream()
                .forEach(notification -> assertTrue(notification.isChecked()));
    }

    @DisplayName("미확인 알림 수")
    @Test
    void count_unchecked_notifications() throws Exception {

        // Given
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/count-unchecked")
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
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
/*
    @DisplayName("알림 확인")
    @WithAccount(NAME)
    @Test
    void getNotification() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        // 강의 승인
        lecture.approve();

        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(user);
        Notification notification = notifications.get(0);
        Long notificationId = notification.getId();

        // When
        mockMvc.perform(put(BASE_URL + "/{notification_id}", notificationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        notification = notificationRepository.findById(notificationId).orElseThrow(RuntimeException::new);
        assertNotNull(notification);
        assertTrue(notification.isChecked());
    }*/

    @Test
    void deleteNotification() throws Exception {

        // Given
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecture.getLecturePrices().get(0).getId());
        List<Notification> notifications = notificationRepository.findByUser(mentorUser);
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
/*
    @WithAccount(NAME)
    @Test
    void deleteNotifications() throws Exception {

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
    }*/
}