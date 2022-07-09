package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.controller.response.NotificationResponse;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.notification.vo.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private final static String BASE_URL = "/api/users/my-notifications";

    @Mock
    NotificationService notificationService;
    @InjectMocks
    NotificationController notificationController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }
/*
    @Test
    void getNotification() throws Exception {

        // given
//        doNothing()
//                .when(notificationService).check(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getNotification_throwException() throws Exception {

        // given
//        doThrow(new EntityNotFoundException(NOTIFICATION))
//                .when(notificationService).check(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }*/

    @Test
    void get_notifications() throws Exception {

        // given
        Notification notification = mock(Notification.class);
        when(notification.getType()).thenReturn(NotificationType.ENROLLMENT);
        Page<NotificationResponse> notifications = new PageImpl<>(Arrays.asList(new NotificationResponse(notification)), Pageable.ofSize(20), 1);
        doReturn(notifications)
                .when(notificationService).getNotificationResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..notificationId").exists())
                .andExpect(jsonPath("$..type").exists())
                .andExpect(jsonPath("$..content").exists())
                .andExpect(jsonPath("$..checked").exists())
                .andExpect(jsonPath("$..createdAt").exists())
                .andExpect(jsonPath("$..checkedAt").exists());
    }

    @Test
    void delete_notification() throws Exception {

        // given
        doNothing()
                .when(notificationService).deleteNotification(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
/*
    @Test
    void delete_notifications() throws Exception {

        // given
        doNothing()
                .when(notificationService).deleteNotifications(any(User.class), anyList());
        // when
        // then
        mockMvc.perform(delete(BASE_URL).param("notification_ids", "1", "2"))
                .andDo(print())
                .andExpect(status().isOk());
    }*/
}