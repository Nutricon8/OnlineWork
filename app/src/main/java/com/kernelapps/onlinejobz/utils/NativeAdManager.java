package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.NativeAd;
import com.kernelapps.onlinejobz.R;

public class NativeAdManager {
    private static final String TAG = "NativeAdManager";

    private Context context;
    private ATNativeAdView adContainer;
    private ATNative atNative;
    private NativeAd currentAd;

    // Internal listeners
    private final ATNativeNetworkListener networkListener = new ATNativeNetworkListener() {
        @Override
        public void onNativeAdLoaded() {
            Log.d(TAG, "Ad loaded successfully");
            renderAd();
        }

        @Override
        public void onNativeAdLoadFail(AdError adError) {
            Log.e(TAG, "Ad load failed: " + adError.getFullErrorInfo());
        }
    };

    private final ATNativeEventListener adEventListener = new ATNativeEventListener() {
        @Override
        public void onAdImpressed(ATNativeAdView view, ATAdInfo atAdInfo) {
            Log.d(TAG, "Ad impressed");
            // Add your impression tracking logic here
        }

        @Override
        public void onAdClicked(ATNativeAdView view, ATAdInfo atAdInfo) {
            Log.d(TAG, "Ad clicked");
            // Add your click tracking logic here
        }

        @Override
        public void onAdVideoStart(ATNativeAdView view) {
            Log.d(TAG, "Video started");
        }

        @Override
        public void onAdVideoEnd(ATNativeAdView view) {
            Log.d(TAG, "Video ended");
        }

        @Override
        public void onAdVideoProgress(ATNativeAdView view, int progress) {
            Log.d(TAG, "Video progress: " + progress + "%");
        }
    };

    public NativeAdManager(Context context, ATNativeAdView adContainer) {
        this.context = context;
        this.adContainer = adContainer;
        initialize();
    }

    private void initialize() {
        adContainer.removeAllViews();
        atNative = new ATNative(context, context.getString(R.string.NATIVE_PLACEMENT_ID), networkListener);
    }

    public void loadAd() {
        if (atNative != null) {
            atNative.makeAdRequest();
        }
    }

    public void destroy() {
        if (currentAd != null) {
            currentAd.destory();
            currentAd = null;
        }

        if (adContainer != null) {
            adContainer.removeAllViews();
        }
    }

    private void renderAd() {
        currentAd = atNative.getNativeAd();
        if (currentAd == null) {
            Log.e(TAG, "No ad available to render");
            return;
        }

        currentAd.setNativeEventListener(adEventListener);

        if (currentAd.isNativeExpress()) {
            // Template rendering
            currentAd.renderAdContainer(adContainer, null);
            currentAd.prepare(adContainer, null);
        } else {
            // Self rendering
            View selfRenderView = View.inflate(context, R.layout.native_ad, null);
            ATNativePrepareInfo prepareInfo = new ATNativePrepareInfo();

            // Bind views (implement this according to your layout)
            SelfRenderViewUtil.bindSelfRenderView(
                    context,
                    currentAd.getAdMaterial(),
                    selfRenderView,
                    prepareInfo
            );

            adContainer.removeAllViews();
            adContainer.addView(selfRenderView);
            currentAd.renderAdContainer(adContainer, selfRenderView);
            currentAd.prepare(adContainer, prepareInfo);
        }
    }
}