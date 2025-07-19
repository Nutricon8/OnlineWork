package com.kernelapps.onlinejobz.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.nativead.api.ATNativeAdView;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.adapters.FAQAdapter;
import com.kernelapps.onlinejobz.models.FAQ;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.NativeAdManager;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Frequently Asked Questions", true);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<FAQ> faqList = new ArrayList<>();
        faqList.add(new FAQ("How do I find the right job for my skills?", "Our app provides tailored recommendations based on your profile, skills, and experience. Use the search filters to narrow down opportunities that match your expertise."));

        faqList.add(new FAQ("What should I include in my resume?", "Focus on relevant work experience, key skills, education, and achievements. Keep it concise (1-2 pages) and tailor it for each job application."));

        faqList.add(new FAQ("How can I make my online job application stand out?", "Customize your cover letter, use keywords from the job description, highlight measurable achievements, and ensure your online profiles (like LinkedIn) are up-to-date."));

        faqList.add(new FAQ("What are some tips for remote job applications?", "Emphasize your self-motivation, time management skills, and previous remote experience (if any). Ensure you have a professional home office setup for potential video interviews."));

        faqList.add(new FAQ("How do I prepare for a job interview?", "Research the company, practice common interview questions, prepare questions to ask the interviewer, and dress appropriately (even for video calls)."));

        faqList.add(new FAQ("What are red flags to watch for in online job postings?", "Be cautious of vague job descriptions, requests for payment, poor grammar/spelling, lack of company information, or promises of unrealistic earnings."));

        faqList.add(new FAQ("How can I leverage online gig platforms effectively?", "Build a strong profile with portfolio samples, start with smaller gigs to build ratings, communicate clearly with clients, and specialize in specific services to stand out."));
        // Add more FAQs
        FAQAdapter adapter = new FAQAdapter(this, faqList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load ads
        //FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        //AdsManager.loadAndShowBanner(this, bannerContainer);
        // In your Activity/Fragment:
        ATNativeAdView adContainer = findViewById(R.id.native_ad_view);
        NativeAdManager adManager = new NativeAdManager(this, adContainer);

        // Simply load the ad - all listeners are internal
        adManager.loadAd();
    }
}