package com.kernelapps.onlinejobz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.MainActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.WalkthroughAdapter;
import com.kernelapps.onlinejobz.utils.AdsManager;

public class WalkthroughActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutIndicators;
    private Button buttonNext;
    private WalkthroughAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isWalkthroughShown", false)) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_walkthrough);

        viewPager = findViewById(R.id.viewPager);
        layoutIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonNext = findViewById(R.id.buttonNext);
        TextView btnSkip = findViewById(R.id.btnSkip);

        adapter = new WalkthroughAdapter(this);
        viewPager.setAdapter(adapter);

        setupIndicators(adapter.getItemCount());
        setCurrentIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                if (position == adapter.getItemCount() - 1) {
                    buttonNext.setText(R.string.continue_string);
                } else {
                    buttonNext.setText(R.string.next);
                }
            }
        });

        buttonNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(current + 1);
            } else {
                finishWalkthrough(); // Last screen clicked
            }
        });

        btnSkip.setOnClickListener(v -> finishWalkthrough());

        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitBottomSheet();
            }
        });
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(params);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.indicator_inactive
                ));
            }
        }
    }

    public void finishWalkthrough() {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isWalkthroughShown", true);
        editor.apply();
        startActivity(new Intent(this, StartActivity.class));
        AdsManager.showInterstitialAd(this);
        finish();
    }
}