package com.kernelapps.onlinejobz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.NotificationsAdapter;
import com.kernelapps.onlinejobz.models.NotificationEntity;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.NotificationStorage;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {

    private NotificationsAdapter adapter;
    private final List<NotificationEntity> notifications = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Notifications", true);

        View emptyStateView = findViewById(R.id.empty_state_view);


        // Setup RecyclerView
        RecyclerView notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(notifications);
        notificationsRecyclerView.setAdapter(adapter);

        // Load notifications from Firestore
        loadNotifications();

        if (notifications.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            notificationsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            notificationsRecyclerView.setVisibility(View.VISIBLE);
        }

        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
    }

    private void loadNotifications() {
        List<NotificationEntity> localNotifications = NotificationStorage.getNotifications(this);

        notifications.clear();
        notifications.addAll(localNotifications);
        adapter.updateNotifications(notifications);

        boolean isEmpty = notifications.isEmpty();
        findViewById(R.id.empty_state_view).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        findViewById(R.id.notifications_recycler_view).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

}