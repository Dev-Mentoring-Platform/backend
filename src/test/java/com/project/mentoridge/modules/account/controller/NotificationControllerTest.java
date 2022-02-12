package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.NOTIFICATION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void getNotification() throws Exception {

        // given
        doNothing()
                .when(notificationService).check(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void getNotification_throwException() throws Exception {

        // given
        doThrow(new EntityNotFoundException(NOTIFICATION))
                .when(notificationService).check(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteNotification() throws Exception {

        // given
        doNothing()
                .when(notificationService).deleteNotification(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{notification_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotifications() throws Exception {

        // given
        doNothing()
                .when(notificationService).deleteNotifications(any(User.class), anyList());
        // when
        // then
        mockMvc.perform(delete(BASE_URL).param("notification_ids", "1", "2"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}