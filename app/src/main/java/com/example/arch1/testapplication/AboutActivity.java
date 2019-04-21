package com.example.arch1.testapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

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
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        color = a.getColor(0, 0);
        a.recycle();

        if (themeName.equals(Theme.DEFAULT)) {
            color = getResources().getColor(R.color.colorMaterialSteelGrey);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals(Theme.MATERIAL_LIGHT)) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.gray));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }

        //setting toolbar style manually
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

}
