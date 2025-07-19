package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.content.Intent;

import com.kernelapps.onlinejobz.R;


public class ShareManager {
    Context context;

    public ShareManager(Context context) {
        this.context = context;
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        String shareMessage = "\nLet me recommend this application to you\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n";
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }
}
