package com.kernelapps.onlinejobz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.JobsAdapter;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity implements JobsAdapter.OnJobClickListener {

    private RecyclerView recyclerView;
    private JobsAdapter adapter;
    private final List<JobItem> jobItemList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get category ID from intent
        categoryId = getIntent().getStringExtra("categoryId");
        String categoryTitle = getIntent().getStringExtra("categoryTitle");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, categoryTitle != null ? categoryTitle : "Jobs", true);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Setup RecyclerView with the adapter
        initializeAdapter();

        // Load jobs from Firebase
        loadJobsFromFirebase();

        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
    }

    private void initializeAdapter() {
        adapter = new JobsAdapter(this, jobItemList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadJobsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        if (categoryId != null && !categoryId.isEmpty()) {
            // Get jobs for specific category
            query = db.collection("categories")
                    .document(categoryId)
                    .collection("jobs")
                    .orderBy("postedDate", Query.Direction.DESCENDING);
        } else {
            // Get all jobs across all categories
            query = db.collectionGroup("jobs")
                    .orderBy("postedDate", Query.Direction.DESCENDING);
        }

        query.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                jobItemList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    JobItem jobItem = new JobItem(
                            document.getId(),
                            document.getString("image"),
                            document.getString("title"),
                            document.getString("description"),
                            false,
                            document.getDate("postedDate"),
                            document.getReference().getParent().getParent().getId()
                    );
                    jobItemList.add(jobItem);
                }

                updateEmptyState();
                adapter.notifyDataSetChanged();
            } else {
                showErrorState();
            }
        });
    }

    private void updateEmptyState() {
        if (jobItemList.isEmpty()) {
            emptyStateText.setText(R.string.no_jobs_found);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            emptyStateText.setVisibility(View.GONE);
        }
    }

    private void showErrorState() {
        emptyStateText.setText(R.string.error_loading_jobs);
        emptyStateText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onJobClick(JobItem jobItem) {
        Intent intent = new Intent(this, JobActivity.class);
        intent.putExtra("jobItem", jobItem);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(JobItem jobItem, int position) {
        // Toggle favorite status locally
        boolean newFavoriteState = !jobItem.getFavorite();
        jobItem.setFavorite(newFavoriteState);

        // Update local favorites
        if (newFavoriteState) {
            FavoritesManager.addFavorite(this, jobItem);
        } else {
            FavoritesManager.removeFavorite(this, jobItem.getId());
        }

        // Update UI immediately
        adapter.notifyItemChanged(position);
    }
}