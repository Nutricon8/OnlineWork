package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.anythink.core.api.IATAdvertiserInfoOperate;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.kernelapps.onlinejobz.R;

import java.util.ArrayList;
import java.util.List;

public class SelfRenderViewUtil {
    private static final String TAG = SelfRenderViewUtil.class.getSimpleName();

    public static void bindSelfRenderView(Context context, ATNativeMaterial adMaterial,
                                          View selfRenderView, ATNativePrepareInfo nativePrepareInfo) {
        printNativeAdMaterial(adMaterial);

        // Initialize prepare info if null
        if (nativePrepareInfo == null) {
            nativePrepareInfo = new ATNativePrepareInfo();
        }

        // Find all views
        ImageView iconView = selfRenderView.findViewById(R.id.native_ad_icon);
        TextView titleView = selfRenderView.findViewById(R.id.native_ad_title);
        TextView advertiserView = selfRenderView.findViewById(R.id.native_ad_advertiser);
        TextView sponsoredLabel = selfRenderView.findViewById(R.id.native_ad_sponsored_label);
        TextView descriptionView = selfRenderView.findViewById(R.id.native_ad_description);
        FrameLayout mediaContainer = selfRenderView.findViewById(R.id.native_ad_media_container);
        ImageView mainImageView = selfRenderView.findViewById(R.id.native_ad_main_image);
        Button ctaButton = selfRenderView.findViewById(R.id.native_ad_call_to_action);
        ImageView privacyIcon = selfRenderView.findViewById(R.id.native_ad_privacy_icon);

        // Apply adaptive styling
        styleAsContent(selfRenderView);
        ensurePolicyElements(adMaterial, selfRenderView);

        List<View> clickViewList = new ArrayList<>();

        // 1. Title (Required)
        bindTextElement(titleView, adMaterial.getTitle(), nativePrepareInfo::setTitleView, clickViewList);

        // 2. Advertiser/Sponsored info (Required)
        if (!TextUtils.isEmpty(adMaterial.getAdvertiserName())) {
            advertiserView.setText(adMaterial.getAdvertiserName());
        } else {
            advertiserView.setText(R.string.sponsored_content);
        }
        clickViewList.add(advertiserView);

        // 3. Description
        bindTextElement(descriptionView, adMaterial.getDescriptionText(),
                nativePrepareInfo::setDescView, clickViewList);

        // 4. Icon (Required)
        bindIcon(context, adMaterial, iconView, mediaContainer, nativePrepareInfo, clickViewList);

        // 5. Media Content (Required for most networks)
        bindMediaContent(context, adMaterial, mediaContainer, mainImageView,
                nativePrepareInfo, clickViewList);

        // 6. Call to Action (Required)
        bindTextElement(ctaButton, adMaterial.getCallToActionText(),
                nativePrepareInfo::setCtaView, clickViewList);

        // 7. Privacy/Ad choices
        setupPrivacyIcon(adMaterial, privacyIcon);

        // 8. Sponsored label (Required)
        sponsoredLabel.setVisibility(View.VISIBLE);

        // Final setup
        nativePrepareInfo.setClickViewList(clickViewList);

        // Handle advertiser info
        setupAdvertiserInfo(adMaterial, advertiserView, clickViewList);

        // For ATNativePrepareExInfo
        if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();
            creativeClickViewList.add(ctaButton);
            ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);
        }
    }

    // ========== Helper Methods ========== //

    private static void bindTextElement(TextView view, String text,
                                        ViewBinder binder, List<View> clickViewList) {
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
            if (binder != null) binder.bind(view);
            clickViewList.add(view);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private static void bindIcon(Context context, ATNativeMaterial adMaterial,
                                 ImageView iconView, FrameLayout mediaContainer,
                                 ATNativePrepareInfo prepareInfo, List<View> clickViewList) {

        View adIconView = adMaterial.getAdIconView();
        String iconUrl = adMaterial.getIconImageUrl();

        if (adIconView != null) {
            // Use network-provided icon view
            if (adIconView.getParent() != null) {
                ((ViewGroup) adIconView.getParent()).removeView(adIconView);
            }
            mediaContainer.addView(adIconView);
            prepareInfo.setIconView(adIconView);
            clickViewList.add(adIconView);
            iconView.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(iconUrl)) {
            // Load icon image
            ATNativeImageView iconImage = new ATNativeImageView(context);
            iconImage.setImage(iconUrl);
            iconView.setImageDrawable(iconImage.getDrawable());
            prepareInfo.setIconView(iconView);
            clickViewList.add(iconView);
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }
    }

    private static void bindMediaContent(Context context, ATNativeMaterial adMaterial,
                                         FrameLayout mediaContainer, ImageView fallbackImage,
                                         ATNativePrepareInfo prepareInfo, List<View> clickViewList) {

        mediaContainer.removeAllViews();

        // 1. Try to use network-provided media view first
        View mediaView = adMaterial.getAdMediaView(mediaContainer);
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            // Calculate proper aspect ratio
            mediaContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mediaContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            //adjustMediaViewSize(mediaContainer, mediaView,
                            //        adMaterial.getMainImageWidth(), adMaterial.getMainImageHeight());
                        }
                    });

            mediaContainer.addView(mediaView);
            clickViewList.add(mediaView);
            fallbackImage.setVisibility(View.GONE);
            return;
        }

        // 2. Try main image
        String mainImageUrl = adMaterial.getMainImageUrl();
        if (!TextUtils.isEmpty(mainImageUrl)) {
            ATNativeImageView mainImage = new ATNativeImageView(context);
            mainImage.setImage(mainImageUrl);
            //adjustMediaViewSize(mediaContainer, mainImage,
            //        adMaterial.getMainImageWidth(), adMaterial.getMainImageHeight());
            mediaContainer.addView(mainImage);
            prepareInfo.setMainImageView(mainImage);
            clickViewList.add(mainImage);
            fallbackImage.setVisibility(View.GONE);
            return;
        }

        // 3. No media available
        fallbackImage.setVisibility(View.GONE);
    }

    /*private static void adjustMediaViewSize(ViewGroup container, View mediaView,
                                            int width, int height) {
        if (width <= 0 || height <= 0) return;

        float aspectRatio = (float) width / height;
        int containerWidth = container.getWidth();
        int calculatedHeight = (int) (containerWidth / aspectRatio);

        ViewGroup.LayoutParams params = mediaView.getLayoutParams();
        params.width = containerWidth;
        params.height = calculatedHeight;
        mediaView.setLayoutParams(params);
    }*/

    private static void setupAdvertiserInfo(ATNativeMaterial adMaterial,
                                            TextView advertiserView, List<View> clickViewList) {
        IATAdvertiserInfoOperate infoOperate = adMaterial.getAdvertiserInfoOperate();
        if (infoOperate != null) {
            advertiserView.setOnClickListener(v ->
                    infoOperate.showAdvertiserInfoDialog(advertiserView, true));
            clickViewList.add(advertiserView);
        }
    }

    private static void setupPrivacyIcon(ATNativeMaterial adMaterial, ImageView privacyIcon) {
        if (privacyIcon == null) return;

        Bitmap adChoiceIcon = adMaterial.getAdLogo();
        if (adChoiceIcon != null) {
            privacyIcon.setImageBitmap(adChoiceIcon);
            privacyIcon.setVisibility(View.VISIBLE);
        } else {
            privacyIcon.setVisibility(View.GONE);
        }
    }

    private static void styleAsContent(View adView) {
        // Match your app's content styling
        //adView.setBackgroundResource(R.drawable.bg_content_card);

        TextView title = adView.findViewById(R.id.native_ad_title);
        //title.setTextAppearance(R.style.TextAppearance_App_Headline);

        TextView desc = adView.findViewById(R.id.native_ad_description);
        //desc.setTextAppearance(R.style.TextAppearance_App_Body);

        Button cta = adView.findViewById(R.id.native_ad_call_to_action);
        //cta.setBackgroundResource(R.drawable.btn_primary);
    }

    private static void ensurePolicyElements(ATNativeMaterial material, View adView) {
        // Required disclosure elements
        TextView sponsored = adView.findViewById(R.id.native_ad_sponsored_label);
        sponsored.setVisibility(View.VISIBLE);

        TextView advertiser = adView.findViewById(R.id.native_ad_advertiser);
        if (TextUtils.isEmpty(material.getAdvertiserName())) {
            advertiser.setText(R.string.sponsored_content);
        }
    }

    private static void printNativeAdMaterial(ATNativeMaterial adMaterial) {
        if (adMaterial == null) return;
        Log.i(TAG, "Native Ad Material:\n" + adMaterial.toString());
    }

    interface ViewBinder {
        void bind(View view);
    }
}