package com.kernelapps.onlinejobz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.nativead.api.ATNativeAdView;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.MainActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.StartedAdapter;
import com.kernelapps.onlinejobz.models.StartedItem;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.NativeAdManager;
import com.kernelapps.onlinejobz.utils.RateManager;
import com.kernelapps.onlinejobz.utils.ShareManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends BaseActivity {
    NativeAdManager adManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<StartedItem> itemList = new ArrayList<>();
        itemList.add(new StartedItem(R.drawable.baseline_play_circle_outline_24, "Let's Get Started", item -> {
            startActivity(new Intent(this, MainActivity.class));
            AdsManager.showInterstitialAd(this);
            finish();
        }));
        itemList.add(new StartedItem(R.drawable.baseline_star_rate_24, "Rate Us", item -> {
            RateManager rateManager = new RateManager(this);
            rateManager.rate();
        }));
        itemList.add(new StartedItem(R.drawable.round_info_24, "Frequently Asked Questions", item -> {
            startActivity(new Intent(this, FaqActivity.class));
            AdsManager.showInterstitialAd(this);
        }));

        itemList.add(new StartedItem(R.drawable.baseline_share_24, "Share App", item -> {
            ShareManager shareManager = new ShareManager(this);
            shareManager.shareApp();
        }));

        StartedAdapter adapter = new StartedAdapter(this, itemList);
        recyclerView.setAdapter(adapter);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitBottomSheet();
            }
        });


        // In your Activity/Fragment:
        ATNativeAdView adContainer = findViewById(R.id.native_ad_view);
        adManager = new NativeAdManager(this, adContainer);

        // Simply load the ad - all listeners are internal
        adManager.loadAd();
    }


    // When destroying:
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adManager.destroy();
    }
}
