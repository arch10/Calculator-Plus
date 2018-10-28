package com.example.arch1.testapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class AboutActivity extends AppCompatActivity {

    private AppPreferences preferences;
    private Toolbar toolbar;
    private TextView version, build, privacy;
    private Context context;
    private int color;

    private static final String PRIVACY_URL = "https://github.com/arch10/Calculator/blob/master/docs/en/privacy_policy.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        version = findViewById(R.id.version);
        build = findViewById(R.id.build);
        privacy = findViewById(R.id.privacy);
        context = this;

        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        color = a.getColor(0, 0);
        if(themeName.equals("default") || themeName.equals(""))
            color = getResources().getColor(R.color.colorMaterialSteelGrey);


        //setting toolbar style manually
        //setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setBackgroundColor(color);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Date buildDate = BuildConfig.buildTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        build.setText("Build Date: " + sdf.format(buildDate));
        version.setText("Version: " + BuildConfig.VERSION_NAME);


        //privacy policy link
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setToolbarColor(color)
                        .setShowTitle(true)
                        .build();
                CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
                CustomTabsHelper.openCustomTab(context, customTabsIntent, Uri.parse(PRIVACY_URL), new WebViewFallback());
            }
        });
    }

    private void setTheme(String themeName) {
        if (themeName.equals("green")) {

            setTheme(R.style.GreenAppTheme);

        } else if (themeName.equals("orange")) {

            setTheme(R.style.AppTheme);

        } else if (themeName.equals("blue")) {

            setTheme(R.style.BlueAppTheme);

        } else if (themeName.equals("red")) {

            setTheme(R.style.RedAppTheme);

        } else if (themeName.equals("lgreen")) {

            setTheme(R.style.LightGreenAppTheme);

        } else if (themeName.equals("pink")) {

            setTheme(R.style.PinkAppTheme);

        } else if (themeName.equals("purple")) {

            setTheme(R.style.PurpleAppTheme);

        } else if (themeName.equals("default")) {

            setTheme(R.style.DefAppTheme);

        } else if (themeName.equals("")) {

            setTheme(R.style.DefAppTheme);
            preferences.setStringPreference(AppPreferences.APP_THEME, "default");

        }
    }

    private int getThemeColor(String themeName) {
        switch (themeName){
            case "green": return getResources().getColor(R.color.colorGreenPrimary);
            case "orange": return getResources().getColor(R.color.colorPrimary);
            case "blue": return getResources().getColor(R.color.colorBluePrimary);
            case "lgreen": return getResources().getColor(R.color.colorLightGreenPrimary);
            case "pink": return getResources().getColor(R.color.colorPinkPrimary);
            case "default": return getResources().getColor(R.color.colorMaterialSteelGrey);
            default: return getResources().getColor(R.color.colorMaterialSteelGrey);
        }
    }
}
