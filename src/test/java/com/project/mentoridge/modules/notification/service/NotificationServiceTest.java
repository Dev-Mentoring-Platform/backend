package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.modules.account.controller.response.NotificationResponse;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    UserRepository userRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    SimpMessageSendingOperations messageSendingTemplate;

    @Test
    void get_paged_NotificationResponses() {

        // given
        User user = mock(User.class);
        when(notificationRepository.findByUserOrderByIdDesc(user, any(Pageable.class))).thenReturn(Page.empty());

        // when
        Page<NotificationResponse> response = notificationService.getNotificationResponses(user, 1);
        // then
        assertThat(response.getContent()).hasSize(0);
    }

    @Test
    void count_unchecked_notifications() {

        // given
        // when
        User user = mock(User.class);
        notificationService.countUncheckedNotifications(user);
        // then
        verify(notificationRepository).countByUserAndCheckedIsFalse(user);
    }

    @Test
    void create_notification_by_userId() {

        // given
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        notificationService.createNotification(1L, NotificationType.ENROLLMENT);

        // then
        verify(notificationRepository).save(any(Notification.class));

        Notification saved = mock(Notification.class);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);
        verify(messageSendingTemplate).convertAndSend(anyString(), any(NotificationMessage.class));
    }

    @Test
    void create_notification() {

        // given
        // when
        User user = mock(User.class);
        notificationService.createNotification(user, NotificationType.ENROLLMENT);

        // then
        verify(notificationRepository).save(any(Notification.class));

        Notification saved = mock(Notification.class);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);
        verify(messageSendingTemplate).convertAndSend(anyString(), any(NotificationMessage.class));
    }
/*
    @Test
    void _check() {
        // user, notificationId

        // given
        User user = mock(User.class);
        Notification notification = Mockito.mock(Notification.class);
        when(notificationRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(notification));

        // when
        notificationService.check(user, 1L);

        // then
        verify(notification).check();

    }

    @Test
    void check() {
        // user, notificationId

        // given
        User user = mock(User.class);
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.CHAT)
                .build();
        assertFalse(notification.isChecked());
        assertNull(notification.getCheckedAt());
        when(notificationRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(notification));

        // when
        notificationService.check(user, 1L);

        // then
        assertTrue(notification.isChecked());
        assertNotNull(notification.getCheckedAt());
    }*/

    @Test
    void check_all() {

        // given
        User user = mock(User.class);
//        Notification notification1 = Notification.builder()
//                .user(user)
//                .type(NotificationType.CHAT)
//                .build();
//        Notification notification2 = Notification.builder()
//                .user(user)
//                .type(NotificationType.ENROLLMENT)
//                .build();
        Notification notification1 = mock(Notification.class);
        Notification notification2 = mock(Notification.class);
        when(notificationRepository.findByUser(user)).thenReturn(Arrays.asList(notification1, notification2));

        // when
        notificationService.checkAll(user);

        // then
        verify(notification1).check();
        verify(notification2).check();
    }

    @Test
    void delete_notification() {
        // user, notificationId

        // given
        User user = Mockito.mock(User.class);
        Notification notification = Mockito.mock(Notification.class);
        when(notificationRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(notification));

        // when
        notificationService.deleteNotification(user, 1L);

        // then
        verify(notificationRepository).delete(notification);
    }
}
