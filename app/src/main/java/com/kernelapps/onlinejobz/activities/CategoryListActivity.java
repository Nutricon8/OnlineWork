package com.kernelapps.onlinejobz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.CategoriesAdapter;
import com.kernelapps.onlinejobz.models.Category;
import com.kernelapps.onlinejobz.utils.AdsManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends BaseActivity {//implements CategoriesAdapter.OnJobItemClickListener {
    private RelativeLayout loadingLayout;
    private final List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Categories", true);

        loadingLayout = findViewById(R.id.loadingLayout);
        RecyclerView categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories, this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Load from Firebase
        loadCategoriesFromFirebase(categoriesAdapter);
        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
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
                        Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}