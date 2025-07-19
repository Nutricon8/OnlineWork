package com.kernelapps.onlinejobz.utils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kernelapps.onlinejobz.models.JobItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseJobManager {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Saves a job to Firestore, preserving the existing ID if provided
     * or generating a new one if not
     */
    public static void saveJob(JobItem jobItem, OnJobOperationListener listener) {
        // Validate required fields
        if (jobItem.getCategoryId() == null || jobItem.getCategoryId().isEmpty()) {
            if (listener != null) {
                listener.onFailure(new IllegalArgumentException("Category ID cannot be null or empty"));
            }
            return;
        }

        // Determine the document reference
        if (jobItem.getId() != null && !jobItem.getId().isEmpty()) {
            // Save with existing ID
            saveJobWithId(jobItem, listener);
        } else {
            // Create new job with generated ID
            createNewJob(jobItem, listener);
        }
    }

    /**
     * Saves a job with a specific ID to Firestore
     */
    private static void saveJobWithId(JobItem jobItem, OnJobOperationListener listener) {
        db.collection("categories")
                .document(jobItem.getCategoryId())
                .collection("jobs")
                .document(jobItem.getId())
                .set(jobItem)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess(jobItem);
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Creates a new job with a generated ID
     */
    private static void createNewJob(JobItem jobItem, OnJobOperationListener listener) {
        String newDocId = db.collection("categories")
                .document(jobItem.getCategoryId())
                .collection("jobs")
                .document()
                .getId();

        jobItem.setId(newDocId);
        saveJobWithId(jobItem, listener);
    }

    /**
     * Creates a new category with initial jobs
     */
    public static void createCategoryWithJobs(String categoryId, String categoryName,
                                              List<JobItem> jobs, OnCategoryOperationListener listener) {
        // Validate inputs
        if (categoryId == null || categoryId.isEmpty()) {
            if (listener != null) {
                listener.onFailure(new IllegalArgumentException("Category ID cannot be null or empty"));
            }
            return;
        }

        // Create category document
        Map<String, Object> category = new HashMap<>();
        category.put("title", categoryName);

        db.collection("categories")
                .document(categoryId)
                .set(category)
                .addOnSuccessListener(aVoid -> {
                    // Save all jobs in the category
                    saveAllJobs(categoryId, jobs, listener);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Saves multiple jobs to a category
     */
    private static void saveAllJobs(String categoryId, List<JobItem> jobs,
                                    OnCategoryOperationListener listener) {
        if (jobs == null || jobs.isEmpty()) {
            if (listener != null) {
                listener.onSuccess();
            }
            return;
        }

        // Track success/failure
        final int[] completed = {0};
        final boolean[] hasFailure = {false};
        final Exception[] firstError = {null};

        for (JobItem job : jobs) {
            job.setCategoryId(categoryId);
            saveJob(job, new OnJobOperationListener() {
                @Override
                public void onSuccess(JobItem jobItem) {
                    completed[0]++;
                    checkCompletion(completed[0], jobs.size(), hasFailure[0], firstError[0], listener);
                }

                @Override
                public void onFailure(Exception e) {
                    completed[0]++;
                    hasFailure[0] = true;
                    if (firstError[0] == null) {
                        firstError[0] = e;
                    }
                    checkCompletion(completed[0], jobs.size(), hasFailure[0], firstError[0], listener);
                }
            });
        }
    }

    private static void checkCompletion(int completed, int total, boolean hasFailure,
                                        Exception firstError, OnCategoryOperationListener listener) {
        if (completed == total && listener != null) {
            if (hasFailure) {
                listener.onFailure(firstError != null ? firstError :
                        new Exception("Failed to save some jobs"));
            } else {
                listener.onSuccess();
            }
        }
    }

    public interface OnJobOperationListener {
        void onSuccess(JobItem jobItem);
        void onFailure(Exception e);
    }

    public interface OnCategoryOperationListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}