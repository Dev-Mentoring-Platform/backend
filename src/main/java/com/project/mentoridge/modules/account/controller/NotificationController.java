package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.response.NotificationResponse;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"NotificationController"})
@RequestMapping("/api/users/my-notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation("알림 리스트 - 페이징")
    @GetMapping
    public ResponseEntity<?> getNotifications(@CurrentUser User user,
                                              @RequestParam(defaultValue = "1") Integer page) {

        Page<NotificationResponse> notifications = notificationService.getNotificationResponses(user, page);
        return ResponseEntity.ok(notifications);
    }

    // TODO - 알림 확인
/*
    @ApiOperation("알림 확인")
    @PutMapping("/{notification_id}")
    public ResponseEntity<?> getNotification(@CurrentUser User user,
                                             @PathVariable(name = "notification_id") Long notificationId) {
        notificationService.check(user, notificationId);
        return ok();
    }
*/

    @ApiOperation("알림 삭제")
    @DeleteMapping("/{notification_id}")
    public ResponseEntity<?> deleteNotification(@CurrentUser User user,
                                                @PathVariable(name = "notification_id") Long notificationId) {
        notificationService.deleteNotification(user, notificationId);
        return ok();
    }
/*
    // TODO - 알림 전체 삭제 / 선택 삭제
    @ApiOperation("알림 선택 삭제")
    @DeleteMapping
    public ResponseEntity<?> deleteNotifications(@CurrentUser User user,
                                                 @RequestParam(value = "notification_ids") List<Long> notificationIds) {
        notificationService.deleteNotifications(user, notificationIds);
        return ok();
    }*/
}
