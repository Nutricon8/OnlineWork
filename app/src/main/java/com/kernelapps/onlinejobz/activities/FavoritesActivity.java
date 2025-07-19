package com.kernelapps.onlinejobz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.JobsAdapter;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoritesActivity extends BaseActivity implements JobsAdapter.OnJobClickListener {

    private RecyclerView recyclerView;
    private JobsAdapter adapter;
    private TextView emptyView;
    private List<JobItem> favoriteJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Favorites", true);
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.textViewEmpty);

        // Setup RecyclerView
        adapter = new JobsAdapter(this, new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load favorites
        loadFavorites();

        // Load ads
        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
    }

    private void loadFavorites() {
        // Get favorites from local storage
        favoriteJobs = FavoritesManager.getFavorites(this);

        if (favoriteJobs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            // Reverse the list to show last added at the top
            List<JobItem> reversedList = new ArrayList<>(favoriteJobs);
            Collections.reverse(reversedList);

            adapter.updateJobs(reversedList);
        }
    }

    @Override
    public void onJobClick(JobItem jobItem) {
        // Open job details
        Intent intent = new Intent(this, JobActivity.class);
        intent.putExtra("jobItem", jobItem);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(JobItem jobItem, int position) {
        // Remove from favorites
        FavoritesManager.removeFavorite(this, jobItem.getId());
        favoriteJobs.remove(position);
        adapter.notifyItemRemoved(position);

        // Update empty state if needed
        if (favoriteJobs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh in case favorites were changed elsewhere
        loadFavorites();
    }
}