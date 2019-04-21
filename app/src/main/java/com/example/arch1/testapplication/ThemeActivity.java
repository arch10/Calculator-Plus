package com.example.arch1.testapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {

    private AppPreferences preferences;
    private View themeSwitcher;
    private LinearLayout classic, orange, green, blue, red, lightGreen, purple, pink, materialLight, materialDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_layout);

        themeSwitcher = findViewById(R.id.theme_switcher);
        classic = themeSwitcher.findViewById(R.id.theme_classic);
        orange = themeSwitcher.findViewById(R.id.theme_orange);
        green = themeSwitcher.findViewById(R.id.theme_green);
        blue = themeSwitcher.findViewById(R.id.theme_blue);
        red = themeSwitcher.findViewById(R.id.theme_red);
        lightGreen = themeSwitcher.findViewById(R.id.theme_lightgreen);
        purple = themeSwitcher.findViewById(R.id.theme_purple);
        pink = themeSwitcher.findViewById(R.id.theme_pink);
        materialLight = themeSwitcher.findViewById(R.id.theme_material_light);
        materialDark = themeSwitcher.findViewById(R.id.theme_material_dark);

        classic.setOnClickListener(this);
        orange.setOnClickListener(this);
        green.setOnClickListener(this);
        blue.setOnClickListener(this);
        red.setOnClickListener(this);
        lightGreen.setOnClickListener(this);
        purple.setOnClickListener(this);
        pink.setOnClickListener(this);
        materialLight.setOnClickListener(this);
        materialDark.setOnClickListener(this);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
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

    }

    private void changeTheme(String themeName) {
        String currTheme = preferences.getStringPreference(AppPreferences.APP_THEME);
        if (currTheme.equals(themeName))
            return;
        Theme.changeTheme(themeName, preferences);
        Intent intent[] = new Intent[3];
        intent[2] = new Intent(this, ThemeActivity.class);
        intent[1] = new Intent(this, SettingsActivity.class);
        intent[0] = new Intent(this, MainActivity.class);
        startActivities(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.theme_classic:
                changeTheme(Theme.DEFAULT);
                break;
            case R.id.theme_orange:
                changeTheme(Theme.ORANGE);
                break;
            case R.id.theme_green:
                changeTheme(Theme.GREEN);
                break;
            case R.id.theme_blue:
                changeTheme(Theme.BLUE);
                break;
            case R.id.theme_red:
                changeTheme(Theme.RED);
                break;
            case R.id.theme_lightgreen:
                changeTheme(Theme.LIGHT_GREEN);
                break;
            case R.id.theme_pink:
                changeTheme(Theme.PINK);
                break;
            case R.id.theme_purple:
                changeTheme(Theme.PURPLE);
                break;
            case R.id.theme_material_light:
                changeTheme(Theme.MATERIAL_LIGHT);
                break;
            case R.id.theme_material_dark:
                changeTheme(Theme.MATERIAL_DARK);
                break;
            default:
                changeTheme(Theme.MATERIAL_LIGHT);
                break;
        }
    }
}
