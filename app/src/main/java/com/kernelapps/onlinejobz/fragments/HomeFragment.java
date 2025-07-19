package com.kernelapps.onlinejobz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.activities.JobActivity;
import com.kernelapps.onlinejobz.adapters.JobsAdapter;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements JobsAdapter.OnJobClickListener {

    private JobsAdapter adapter;
    private final List<JobItem> jobItemList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView and Adapter
        adapter = new JobsAdapter(requireContext(), jobItemList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Swipe to refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::loadJobsFromFirebase);

        // Initial load
        loadJobsFromFirebase();
    }

    private void loadJobsFromFirebase() {
        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        emptyStateText.setVisibility(View.GONE);

        db.collectionGroup("jobs")
                //.orderBy("postedDate", Query.Direction.DESCENDING)
                //.limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        jobItemList.clear();
                        List<JobItem> favorites = FavoritesManager.getFavorites(requireContext());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            JobItem jobItem = document.toObject(JobItem.class);
                            jobItem.setId(document.getId());
                            jobItem.setTitle(document.getString("title"));
                            jobItem.setDescription(document.getString("description"));
                            jobItem.setCategoryId(document.getReference().getParent().getParent().getId());
                            jobItem.setFavorite(isJobInFavorites(jobItem.getId(), favorites));

                            jobItemList.add(jobItem);
                        }

                        updateEmptyState();
                        adapter.notifyDataSetChanged();
                    } else {
                        showErrorState();
                    }
                });
    }

    private boolean isJobInFavorites(String jobId, List<JobItem> favorites) {
        for (JobItem favorite : favorites) {
            if (favorite.getId().equals(jobId)) {
                return true;
            }
        }
        return false;
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
        Intent intent = new Intent(requireActivity(), JobActivity.class);
        intent.putExtra("jobItem", jobItem);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(JobItem jobItem, int position) {
        boolean newFavoriteState = !jobItem.getFavorite();
        jobItem.setFavorite(newFavoriteState);

        if (newFavoriteState) {
            FavoritesManager.addFavorite(requireContext(), jobItem);
        } else {
            FavoritesManager.removeFavorite(requireContext(), jobItem.getId());
        }

        adapter.notifyItemChanged(position);
    }
}
