package com.kernelapps.onlinejobz.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.utils.AdsManager;

public class SettingsActivity extends BaseActivity {

    private RadioGroup themeRadioGroup;
    private CheckBox pushNotificationsCheckBox, emailNotificationsCheckBox, inAppNotificationsCheckBox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Settings", true);

        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        pushNotificationsCheckBox = findViewById(R.id.pushNotificationsCheckBox);
        emailNotificationsCheckBox = findViewById(R.id.emailNotificationsCheckBox);
        inAppNotificationsCheckBox = findViewById(R.id.inAppNotificationsCheckBox);

        loadThemePreferences();
        loadNotificationPreferences();

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedTheme = "";
            if (checkedId == R.id.useDeviceThemeRadioButton) {
                selectedTheme = "Device Theme";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (checkedId == R.id.lightThemeRadioButton) {
                selectedTheme = "Light Theme";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.darkThemeRadioButton) {
                selectedTheme = "Dark Theme";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedTheme", selectedTheme);
            editor.apply();

            Toast.makeText(this, "Theme selected: " + selectedTheme, Toast.LENGTH_SHORT).show();
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("pushNotifications", pushNotificationsCheckBox.isChecked());
            editor.putBoolean("emailNotifications", emailNotificationsCheckBox.isChecked());
            editor.putBoolean("inAppNotifications", inAppNotificationsCheckBox.isChecked());
            editor.apply();

            //runInterstitialAds();
            Toast.makeText(this, "Preferences saved.", Toast.LENGTH_SHORT).show();
        });

        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
    }

    private void loadThemePreferences() {
        String savedTheme = sharedPreferences.getString("selectedTheme", "Device Theme");
        switch (savedTheme) {
            case "Light Theme":
                themeRadioGroup.check(R.id.lightThemeRadioButton);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark Theme":
                themeRadioGroup.check(R.id.darkThemeRadioButton);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                themeRadioGroup.check(R.id.useDeviceThemeRadioButton);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }


    private void loadNotificationPreferences() {
        pushNotificationsCheckBox.setChecked(sharedPreferences.getBoolean("pushNotifications", false));
        emailNotificationsCheckBox.setChecked(sharedPreferences.getBoolean("emailNotifications", false));
        inAppNotificationsCheckBox.setChecked(sharedPreferences.getBoolean("inAppNotifications", false));
    }
}