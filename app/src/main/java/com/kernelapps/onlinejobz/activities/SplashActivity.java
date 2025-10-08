package com.kernelapps.onlinejobz.activities;

import static com.anythink.network.adx.AdxATConst.DEBUGGER_CONFIG.Adx_NETWORK;
//import static com.anythink.core.api.ATSDK.ADX_NETWORK_FIRM_ID; // Correct import

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.anythink.core.api.ATDebuggerConfig;
import com.anythink.core.api.ATSDK;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.utils.AdsManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    ProgressBar progressBar;
    int progressStatus = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.horizontal_progress);

        // Initialize TopOn SDK
        ATSDK.init(this, getString(R.string.TOPON_APP_ID), getString(R.string.TOPON_APP_KEY));
        //AdsManager.initializeTopOn(this);
        ATSDK.setNetworkLogDebug(true);// Keep this or set to false for production

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());
        //ATSDK.setDebuggerConfig(this,"825cfd32-9526-4faa-a95a-1235291307fa" , new ATDebuggerConfig.Builder(Adx_NETWORK).build());



        // Initialize TopOn SDK
        // //ATSDK.init(this, getString(R.string.TOPON_APP_ID), getString(R.string.TOPON_APP_KEY));
        // //ATSDK.setNetworkLogDebug(true); // Keep this or set to false for production

        // Initialize Facebook SDK
        // //FacebookSdk.sdkInitialize(this);
        // //AppEventsLogger.activateApp(getApplication());

       // REMOVE OR COMMENT OUT for production:
       //ATSDK.setDebuggerConfig(this,"825cfd32-9526-4faa-a95a-1235291307fa" , new ATDebuggerConfig.Builder(Adx_NETWORK).build());


        // Optional: Verify integration
        //ATSDK.integrationChecking(this);



        //ATSDK.setDebuggerConfig(this,"825cfd32-9526-4faa-a95a-1235291307fa" , new ATDebuggerConfig.Builder(Adx_NETWORK).build());
        // //ATSDK.setDebuggerConfig(this, "825cfd32-9526-4faa-a95a-1235291307fa",
        // //new ATDebuggerConfig.Builder(ATSDK.NONPERSONALIZED).build());   //getGDPRDataLevel(this);

        // Or check what constants are available:
        //ATSDK.setDebuggerConfig(this, "825cfd32-9526-4faa-a95a-1235291307fa",
        //new ATDebuggerConfig.Builder(15).build()); // ADX is usually ID 15

        new Thread(() -> {
            try {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(() -> progressBar.setProgress(progressStatus));
                    //Thread.sleep(20); // 20ms per step = 2 seconds total
                    Thread.sleep(50); // 50ms * 100 = 5000ms = 5 seconds
                }

                // Navigate after full load
                handler.post(() -> {
                    Intent intent = new Intent(SplashActivity.this, WalkthroughActivity.class);
                    startActivity(intent);
                    finish();
                    AdsManager.showInterstitialAd(this);
                });

            } catch (InterruptedException e) {
                // Handle interruption gracefully (optional logging or cleanup)
                e.printStackTrace();
            }
        }).start();
    }
}