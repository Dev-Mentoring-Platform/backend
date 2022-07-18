package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class NotificationControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/users/my-notifications";

    @MockBean
    NotificationService notificationService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
//        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
//                .addFilter(jwtRequestFilter)
//                .addInterceptors(authInterceptor)
//                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
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
    void get_paged_notifications() throws Exception {

        // given
/*
        Notification notification = mock(Notification.class);
        when(notification.getType()).thenReturn(NotificationType.ENROLLMENT);
        Page<NotificationResponse> notifications = new PageImpl<>(Arrays.asList(new NotificationResponse(notification)), Pageable.ofSize(20), 1);
        doReturn(notifications)
                .when(notificationService).getNotificationResponses(any(User.class), 1);*/
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$..notificationId").exists())
//                .andExpect(jsonPath("$..type").exists())
//                .andExpect(jsonPath("$..content").exists())
//                .andExpect(jsonPath("$..checked").exists())
//                .andExpect(jsonPath("$..createdAt").exists())
//                .andExpect(jsonPath("$..checkedAt").exists());
        verify(notificationService).getNotificationResponses(any(User.class), 1);
    }

    @Test
    void check_all_notifications() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(notificationService).checkAll(any(User.class));
    }

    @Test
    void count_unchecked_notifications() throws Exception {

        // given
        doReturn(3L)
                .when(notificationService).countUncheckedNotifications(any(User.class));

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/count-unchecked")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void delete_notification() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{notification_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(notificationService).deleteNotification(any(User.class), 1L);
    }
/*
    @Test
    void delete_notifications() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL).param("notification_ids", "1", "2"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(notificationService).deleteNotifications(any(User.class), anyList());
    }*/
}