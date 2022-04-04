package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.response.NotificationResponse;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import com.project.mentoridge.modules.notification.vo.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.NOTIFICATION;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService extends AbstractService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

        private Page<Notification> getNotifications(User user, Integer page) {
            return notificationRepository.findByUser(user, PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending()));
        }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationResponses(User user, Integer page) {
        return getNotifications(user, page).map(NotificationResponse::new);
    }

    public Notification createNotification(Long userId, NotificationType type) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER));
        return this.createNotification(user, type);
    }

    public Notification createNotification(User user, NotificationType type) {

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .build();
        return notificationRepository.save(notification);
    }

    public void check(User user, Long notificationId) {
        Notification notification = notificationRepository.findByUserAndId(user, notificationId)
                .orElseThrow(() -> new EntityNotFoundException(NOTIFICATION));
        notification.check();
    }

    public void deleteNotification(User user, Long notificationId) {

        // TODO - CHECK : 쿼리 비교 - notificationRepository.deleteById(notificationId);
        Notification notification = notificationRepository.findByUserAndId(user, notificationId)
                .orElseThrow(() -> new EntityNotFoundException(NOTIFICATION));
        notificationRepository.delete(notification);
    }

    public void deleteNotifications(User user, List<Long> notificationIds) {
        // TODO - CHECK
        notificationRepository.deleteAllById(notificationIds);
        // notificationRepository.deleteAllByIdInBatch(notificationIds);
    }
}
