package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kernelapps.onlinejobz.models.JobItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREF_NAME = "favorites_pref";
    private static final String KEY_FAVORITES = "job_favorites";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static List<JobItem> loadFavorites(Context context) {
        SharedPreferences prefs = getPrefs(context);
        String json = prefs.getString(KEY_FAVORITES, null);
        if (json != null) {
            Type type = new TypeToken<List<JobItem>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private static void saveFavorites(Context context, List<JobItem> jobList) {
        SharedPreferences prefs = getPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(jobList);
        editor.putString(KEY_FAVORITES, json);
        editor.apply();
    }

    // CREATE
    public static void addFavorite(Context context, JobItem jobItem) {
        List<JobItem> jobList = loadFavorites(context);
        jobList.add(jobItem);
        saveFavorites(context, jobList);
    }

    // READ
    public static List<JobItem> getFavorites(Context context) {
        return loadFavorites(context);
    }

    // UPDATE
    public static void updateFavorite(Context context, JobItem updatedJob) {
        List<JobItem> jobList = loadFavorites(context);
        for (int i = 0; i < jobList.size(); i++) {
            if (jobList.get(i).getId().equals(updatedJob.getId())) {
                jobList.set(i, updatedJob);
                break;
            }
        }
        saveFavorites(context, jobList);
    }

    // DELETE
    public static void removeFavorite(Context context, String jobId) {
        List<JobItem> jobList = loadFavorites(context);
        jobList.removeIf(item -> item.getId().equals(jobId));
        saveFavorites(context, jobList);
    }

    // EXTRA: Check if item is favorite
    public static boolean isFavorite(Context context, String jobId) {
        List<JobItem> jobList = loadFavorites(context);
        for (JobItem item : jobList) {
            if (item.getId().equals(jobId)) {
                return true;
            }
        }
        return false;
    }
}
