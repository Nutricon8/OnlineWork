package com.kernelapps.onlinejobz.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.activities.JobActivity;
import com.kernelapps.onlinejobz.adapters.JobsAdapter;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements JobsAdapter.OnJobClickListener {

    private RecyclerView recyclerView;
    private JobsAdapter adapter;
    private TextView emptyView;
    private final List<JobItem> favoriteJobs = new ArrayList<>();

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.textViewEmpty);

        // Setup RecyclerView with click listener
        adapter = new JobsAdapter(requireContext(), favoriteJobs, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Load favorites
        loadFavorites();
    }

    /*private void loadFavorites() {
        // Get favorites from local storage
        favoriteJobs.clear();
        favoriteJobs.addAll(FavoritesManager.getFavorites(requireContext()));

        if (favoriteJobs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            showFavorites();
        }
    }*/

    private void loadFavorites() {
        // Get favorites from local storage and reverse them
        List<JobItem> fetchedFavorites = FavoritesManager.getFavorites(requireContext());
        favoriteJobs.clear();

        if (fetchedFavorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Reverse the order
            List<JobItem> reversed = new ArrayList<>(fetchedFavorites);
            java.util.Collections.reverse(reversed);
            favoriteJobs.addAll(reversed);

            showFavorites();
        }
    }



    private void showFavorites() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onJobClick(JobItem jobItem) {
        // Open job details
        Intent intent = new Intent(requireContext(), JobActivity.class);
        intent.putExtra("jobItem", jobItem);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(JobItem jobItem, int position) {
        // Remove from favorites
        FavoritesManager.removeFavorite(requireContext(), jobItem.getId());
        favoriteJobs.remove(position);

        // Update RecyclerView
        if (favoriteJobs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh in case favorites were changed elsewhere
        loadFavorites();
    }
}