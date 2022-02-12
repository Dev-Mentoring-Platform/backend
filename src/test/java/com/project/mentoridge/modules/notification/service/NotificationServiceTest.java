package com.project.mentoridge.modules.notification.service;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void _check() {
        // user, notificationId

        // given
        Notification notification = Mockito.mock(Notification.class);
        when(notificationRepository.findByUserAndId(any(User.class), any(Long.class)))
                .thenReturn(Optional.of(notification));
        // when
        User user = Mockito.mock(User.class);
        notificationService.check(user, 1L);

        // then
        verify(notification).check();

    }

    @Test
    void check() {
        // user, notificationId

        // given
        User user = Mockito.mock(User.class);
        Notification notification = Notification.of(user, NotificationType.CHAT);
        assertFalse(notification.isChecked());
        assertNull(notification.getCheckedAt());

        when(notificationRepository.findByUserAndId(any(User.class), any(Long.class)))
                .thenReturn(Optional.of(notification));

        // when
        notificationService.check(user, 1L);

        // then
        assertTrue(notification.isChecked());
        assertNotNull(notification.getCheckedAt());
    }

    @Test
    void deleteNotification() {
        // user, notificationId

        // given
        Notification notification = Mockito.mock(Notification.class);
        when(notificationRepository.findByUserAndId(any(User.class), any(Long.class)))
                .thenReturn(Optional.of(notification));

        // when
        User user = Mockito.mock(User.class);
        notificationService.deleteNotification(user, 1L);

        // then
        verify(notificationRepository).delete(any(Notification.class));
    }
}
