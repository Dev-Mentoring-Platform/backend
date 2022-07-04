package com.project.mentoridge.modules.notification.service;

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
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    SimpMessageSendingOperations messageSendingTemplate;

    @Test
    void create_notification() {

        // given
        Notification saved = mock(Notification.class);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        // when
        notificationService.createNotification(any(User.class), any(NotificationType.class));

        // then
        verify(notificationRepository).save(any(Notification.class));
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
        Notification notification1 = Notification.builder()
                .user(user)
                .type(NotificationType.CHAT)
                .build();
        Notification notification2 = Notification.builder()
                .user(user)
                .type(NotificationType.ENROLLMENT)
                .build();
        when(notificationRepository.findByUser(user)).thenReturn(Arrays.asList(notification1, notification2));

        // when
        notificationService.checkAll(user);

        // then
        assertTrue(notification1.isChecked());
        assertTrue(notification2.isChecked());
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
