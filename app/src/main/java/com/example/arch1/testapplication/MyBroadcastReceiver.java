package com.example.arch1.testapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals("DOWNLOAD_CLICKED")) {

            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            NotificationManagerCompat.from(context).cancel(56659);

            String url = intent.getStringExtra("url");
            Toast.makeText(context, url, Toast.LENGTH_LONG).show();

            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                    .setShowTitle(true)
                    .build();
            CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
            CustomTabsHelper.openCustomTab(context, customTabsIntent, Uri.parse(url), new WebViewFallback());

        } else if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent repeatingIntent = new Intent(context, MyBroadcastReceiver.class);
            PendingIntent repeatingPendingIntent = PendingIntent.getBroadcast(context,
                    0, repeatingIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            int intevalinMin = 5;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                    intevalinMin * 1000, repeatingPendingIntent);
        } else {
            Intent background = new Intent(context, UpdateService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(background);
            } else
                context.startService(background);
        }
    }
}
