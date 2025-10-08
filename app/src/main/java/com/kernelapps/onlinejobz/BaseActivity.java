package com.kernelapps.onlinejobz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kernelapps.onlinejobz.utils.AdsManager;


public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        AdsManager.loadInterstitial(this);
        loadThemePreferences();
        initNotificationSettings();
        startBackgroundJobs();

        onBaseReady();
    }

    /**
     * Method to be overridden by child activities for their own logic after base setup.
     */
    protected void onBaseReady() {

    }


    private void loadThemePreferences() {
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString("selectedTheme", "Device Theme");

        switch (savedTheme) {
            case "Light Theme":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);  // Light mode
                break;
            case "Dark Theme":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);  // Dark mode
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);  // Follow system theme
                break;
        }
    }


    private void initNotificationSettings() {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications", true);
        // Example logic (you can customize)
        if (!notificationsEnabled) {
            // Disable notifications or cancel existing ones
        }
    }

    private void startBackgroundJobs() {
        // Placeholder for background job setup (e.g., WorkManager, AlarmManager)
        // Example: sync with server, analytics, etc.
    }


    // Function to setup toolbar
    protected void setupToolbar(Toolbar toolbar, String title, boolean showBackButton) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            if (showBackButton) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }

    // Optional: override this in subclasses if you want to handle toolbar back press
    protected void onToolbarBackPressed() {
        AdsManager.showInterstitialAd(this);
        finish();
    }

    protected void showExitBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.exit_bottom_sheet, null, false);

        bottomSheetDialog.setContentView(sheetView);

        Button btnYes = sheetView.findViewById(R.id.btnExitYes);
        Button btnNo = sheetView.findViewById(R.id.btnExitNo);

        btnYes.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            finish(); // Close the app
        });

        btnNo.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // Called when toolbar back (up) button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onToolbarBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdsManager.clearInterstitialListener();
        AdsManager.destroyBanner();
    }

}

