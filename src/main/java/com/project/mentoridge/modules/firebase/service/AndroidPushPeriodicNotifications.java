package com.project.mentoridge.modules.firebase.service;

import org.json.JSONArray;
import org.json.JSONObject;

public class AndroidPushPeriodicNotifications {

    private static final String FCM_TOKENS = "registration_ids";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String NOTIFICATION = "notification";

    public static String PeriodicNotificationJson(String fcmToken, String title, String content) {

        JSONObject body = new JSONObject();

        JSONArray array = new JSONArray();
        array.put(fcmToken);
        body.put(FCM_TOKENS, array);

        JSONObject notification = new JSONObject();
        notification.put(TITLE, title);
        notification.put(BODY, content);
        body.put(NOTIFICATION, notification);

        return body.toString();
    }
}