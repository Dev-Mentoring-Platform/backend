package com.project.mentoridge.modules.firebase.controller;

/*
@Api(tags = {"PushNotificationController"})
@RequiredArgsConstructor
@RestController
public class PushNotificationController {

    private final AndroidPushNotificationsService androidPushNotificationsService;
    private final UserService userService;

    @ApiIgnore
    @GetMapping(value = "/send")
    public ResponseEntity<?> send() throws ExecutionException, InterruptedException {

        CompletableFuture<String> pushNotification
                = androidPushNotificationsService.send("fcmToken", "title", "content");
        CompletableFuture.allOf(pushNotification).join();

        return ResponseEntity.ok(pushNotification.get());
    }

    // 콜백
    @ApiIgnore
    @GetMapping("/set-fcmToken")
    public ResponseEntity<?> setFcmToken(@RequestParam(name = "username") String username,
                                         @RequestParam(name = "fcmToken") String fcmToken) {
        // System.out.println("fcmToken = " + fcmToken);
        userService.updateUserFcmToken(username, fcmToken);
        return ok();
    }
}
*/
