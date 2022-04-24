package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
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
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class NotificationControllerIntegrationTest {

    private final static String BASE_URL = "/api/users/my-notifications";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

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
    EnrollmentService enrollmentService;
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


    @DisplayName("멘티가 강의 수강 시 멘토에게 알림이 오는지 확인")
    @WithAccount(NAME)
    @Test
    void getNotifications_enrollment() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        // 강의 승인
        lecture.approve();

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
/*
    @DisplayName("알림 확인")
    @WithAccount(NAME)
    @Test
    void getNotification() throws Exception {

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
        mockMvc.perform(put(BASE_URL + "/{notification_id}", notificationId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        notification = notificationRepository.findById(notificationId).orElse(null);
        assertNotNull(notification);
        assertTrue(notification.isChecked());
    }*/

    @WithAccount(NAME)
    @Test
    void deleteNotification() throws Exception {

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
        User user = userRepository.findByUsername(USERNAME).orElse(null);
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