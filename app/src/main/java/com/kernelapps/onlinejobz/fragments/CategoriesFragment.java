package com.kernelapps.onlinejobz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.CategoriesAdapter;
import com.kernelapps.onlinejobz.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RelativeLayout loadingLayout;
    private final List<Category> categories = new ArrayList<>();

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingLayout = view.findViewById(R.id.loadingLayout);
        RecyclerView categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories, requireContext());
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Load from Firebase
        loadCategoriesFromFirebase(categoriesAdapter);
    }


    private void loadCategoriesFromFirebase(CategoriesAdapter adapter) {
        loadingLayout.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    loadingLayout.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        categories.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            categories.add(new Category(id, title));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}