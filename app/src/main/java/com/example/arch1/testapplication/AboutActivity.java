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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutActivity extends AppCompatActivity {

    private Context context;
    private int color;

    private static final String PRIVACY_URL = "https://raw.githubusercontent.com/arch10/Calculator-Plus/master/docs/en/privacy_policy.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView version = findViewById(R.id.version);
        TextView build = findViewById(R.id.build);
        TextView privacy = findViewById(R.id.privacy);
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

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Date buildDate = BuildConfig.buildTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        build.setText(getString(R.string.release_date) + " " + sdf.format(buildDate));
        version.setText(getString(R.string.version) + " " + BuildConfig.VERSION_NAME);


        //privacy policy link
        privacy.setOnClickListener(v -> {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setToolbarColor(color)
                    .setShowTitle(true)
                    .build();
            CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
            CustomTabsHelper.openCustomTab(context, customTabsIntent, Uri.parse(PRIVACY_URL), new WebViewFallback());
        });
    }

}
