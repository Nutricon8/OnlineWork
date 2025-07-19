package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kernelapps.onlinejobz.models.NotificationEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationStorage {

    private static final String PREF_NAME = "notification_storage";
    private static final String KEY_NOTIFICATIONS = "notifications";

    public static void saveNotification(Context context, NotificationEntity notification) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        List<NotificationEntity> notifications = getNotifications(context);
        notifications.add(0, notification); // add to top

        String json = gson.toJson(notifications);
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply();
    }

    public static List<NotificationEntity> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIFICATIONS, null);

        if (json == null) return new ArrayList<>();

        Type listType = new TypeToken<List<NotificationEntity>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_NOTIFICATIONS).apply();
    }
}
