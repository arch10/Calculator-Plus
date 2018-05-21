package com.example.arch1.testapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder;

            //registering notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Default";
                String description = "Default channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("Default", name, importance);
                channel.setDescription(description);
                manager.createNotificationChannel(channel);
            }

            //Ongoing Notification
            mBuilder = new NotificationCompat.Builder(context, "Default")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Checking Update")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setProgress(0, 0, true);
            startForeground(001, mBuilder.build());

            //checking update status
            String res = "";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/repos/arch10/calculator/releases/latest")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    res = response.body().string();
                } else {
                    res = "Failed";
                }
            } catch (IOException e) {
                e.printStackTrace();
                res = "Failed";
            }

            parseUpdateJSONResponse(res);

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private void parseUpdateJSONResponse(String response) {
        String version = "";
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!version.equals("") && !response.equals("Failed")) {
            version = "v" + version;
            String latestVersion, prerelease, url;
            try {
                JSONObject jsonObject = new JSONObject(response);
                latestVersion = jsonObject.getString("tag_name");
                prerelease = jsonObject.getString("prerelease");

                if (version.equals(latestVersion) && prerelease.equals("false")) {
                    //result.setText("Update Available");
                    JSONArray assets = jsonObject.getJSONArray("assets");
                    JSONObject object = assets.getJSONObject(0);
                    url = object.getString("browser_download_url");
                    String count = object.getString("download_count");
                    popUpdateNotification(url);
                    //Toast.makeText(this,url+" :: "+count,Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void popUpdateNotification(String url) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;

        //registering notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default";
            String description = "Default channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }

        //adding action to download click in notification
        Intent downloadIntent = new Intent(context, MyBroadcastReceiver.class);
        downloadIntent.setAction("DOWNLOAD_CLICKED");
        downloadIntent.putExtra("url", url);
        PendingIntent downloadPendingIntent = PendingIntent.getBroadcast(context,
                0, downloadIntent, 0);

        //building Notification builder
        mBuilder = new NotificationCompat.Builder(context, "Default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Update Available!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("New update for the app is available. Click here to " +
                                "download the update now."))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setColorized(true)
                .addAction(R.drawable.ic_file_download_black_24dp,
                        "Download", downloadPendingIntent);

        //showing notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(56659, mBuilder.build());
    }

}
