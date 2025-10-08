package com.kernelapps.onlinejobz.utils;


import android.app.Activity;
import android.widget.FrameLayout;

import com.anythink.banner.api.ATBannerListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.kernelapps.onlinejobz.R;

public class AdsManager {

    private static ATInterstitial mInterstitialAd;
    private static ATBannerView mBannerView;

    // Load Interstitial
    public static void loadInterstitial(Activity activity) {
        if (mInterstitialAd == null) {
            mInterstitialAd = new ATInterstitial(activity, activity.getString(R.string.INTERSTITIAL_PLACEMENT_ID));
        }

        mInterstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                // Ad loaded
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                // Ad failed
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo atAdInfo) {}

            @Override
            public void onInterstitialAdShow(ATAdInfo atAdInfo) {}

            @Override
            public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                loadInterstitial(activity); // Reload after close
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {}

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {}

            @Override
            public void onInterstitialAdVideoError(AdError adError) {}
        });

        mInterstitialAd.load();
    }

    // Show Interstitial
    public static void showInterstitialAd(Activity activity) {
        if (mInterstitialAd != null && mInterstitialAd.isAdReady()) {
            mInterstitialAd.show(activity);
        }
    }

    // Load and show banner
    public static void loadAndShowBanner(Activity activity, FrameLayout bannerContainer) {
        if (mBannerView != null) {
            mBannerView.destroy();
        }

        mBannerView = new ATBannerView(activity);
        mBannerView.setPlacementId(activity.getString(R.string.BANNER_PLACEMENT_ID));


        //mBannerView.setBannerAdSize(ATBannerView.BANNER_SIZE_320x50); // Or any other size

        // Set custom 320x90 size using layout parameters
        /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(320, activity),  // Convert 320dp to pixels
                dpToPx(90, activity)    // Convert 90dp to pixels
        );*/
        //mBannerView.setLayoutParams(params);


        mBannerView.setBannerAdListener(new ATBannerListener() {
            @Override
            public void onBannerLoaded() {}

            @Override
            public void onBannerFailed(AdError adError) {}

            @Override
            public void onBannerClicked(ATAdInfo atAdInfo) {}

            @Override
            public void onBannerShow(ATAdInfo atAdInfo) {}

            @Override
            public void onBannerClose(ATAdInfo atAdInfo) {}

            @Override
            public void onBannerAutoRefreshed(ATAdInfo atAdInfo) {}

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {}
        });

        bannerContainer.removeAllViews();
        bannerContainer.addView(mBannerView);
        mBannerView.loadAd();
    }

    private static int dpToPx(int dp, Activity activity) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static void destroyBanner() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    public static void clearInterstitialListener() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
        }
    }
}
