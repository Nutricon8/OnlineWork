package com.kernelapps.onlinejobz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.kernelapps.onlinejobz.activities.CategoryListActivity;
import com.kernelapps.onlinejobz.activities.ContactActivity;
import com.kernelapps.onlinejobz.activities.FaqActivity;
import com.kernelapps.onlinejobz.activities.FavoritesActivity;
import com.kernelapps.onlinejobz.activities.NotificationsActivity;
import com.kernelapps.onlinejobz.activities.SettingsActivity;
import com.kernelapps.onlinejobz.adapters.FragmentsAdapter;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.RateManager;
import com.kernelapps.onlinejobz.utils.ShareManager;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawer;
    ActionBar actionBar;
    private boolean isFirstLaunch = true;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        toolbar.setTitle(getString(R.string.app_name));

        if (isFirstLaunch) {
            //AdsManager.showInterstitialAd(this);
            isFirstLaunch = false; // prevent future triggers in the same instance
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentsAdapter(this, getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitBottomSheet();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_notifications:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, NotificationsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.nav_categories:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, CategoryListActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.nav_favorites:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, FavoritesActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.nav_contact:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, ContactActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.nav_share_app:
                drawer.closeDrawer(GravityCompat.START);
                ShareManager shareManager = new ShareManager(this);
                shareManager.shareApp();
                return true;
            case R.id.nav_rate:
                drawer.closeDrawer(GravityCompat.START);
                RateManager rateManager = new RateManager(this);
                rateManager.rate();
                return true;
            case R.id.nav_faqs:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, FaqActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.nav_settings:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
        return true;
    }
}
