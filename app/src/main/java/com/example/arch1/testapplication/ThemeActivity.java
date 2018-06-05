package com.example.arch1.testapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

public class ThemeActivity extends AppCompatActivity {

    private RadioGroup themeGroup;
    private AppPreferences preferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting toolbar style manually
        setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        themeGroup = findViewById(R.id.rg_theme_group);
        checkSelectedTheme();

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_theme_green:
                        changeTheme("green");
                        break;
                    case R.id.rb_theme_orange:
                        changeTheme("orange");
                        break;
                    case R.id.rb_theme_blue:
                        changeTheme("blue");
                        break;
                    case R.id.rb_theme_lightgreen:
                        changeTheme("lgreen");
                        break;
                    case R.id.rb_theme_pink:
                        changeTheme("pink");
                        break;
                    case R.id.rb_theme_default:
                        changeTheme("default");
                }
            }
        });

    }

    private void changeTheme(String themeName) {

        if (themeName.equals("green")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "green");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);

            startActivities(intent);
            finish();

        } else if (themeName.equals("orange")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "orange");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);
            startActivities(intent);
            finish();
        } else if (themeName.equals("blue")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "blue");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);
            startActivities(intent);
            finish();
        }else if (themeName.equals("lgreen")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "lgreen");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);
            startActivities(intent);
            finish();
        }else if (themeName.equals("pink")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "pink");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);
            startActivities(intent);
            finish();
        } else if (themeName.equals("default")) {

            preferences.setStringPreference(AppPreferences.APP_THEME, "default");
            Intent intent[] = new Intent[3];
            intent[2] = new Intent(this, ThemeActivity.class);
            intent[1] = new Intent(this, SettingsActivity.class);
            intent[0] = new Intent(this, MainActivity.class);
            startActivities(intent);
            finish();
        }

    }

    private void setTheme(String themeName) {
        if (themeName.equals("green")) {

            setTheme(R.style.GreenAppTheme);


        } else if (themeName.equals("orange")) {

            setTheme(R.style.AppTheme);

        } else if (themeName.equals("blue")) {

            setTheme(R.style.BlueAppTheme);

        } else if (themeName.equals("lgreen")) {

            setTheme(R.style.LightGreenAppTheme);

        } else if (themeName.equals("pink")) {

            setTheme(R.style.PinkAppTheme);

        } else if (themeName.equals("default")) {

            setTheme(R.style.DefAppTheme);

        } else if (themeName.equals("")) {

            setTheme(R.style.DefAppTheme);
            preferences.setStringPreference(AppPreferences.APP_THEME, "default");

        }
    }

    private void checkSelectedTheme() {
        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        if (themeName.equals("green")) {

            themeGroup.check(R.id.rb_theme_green);

        } else if (themeName.equals("orange")) {

            themeGroup.check(R.id.rb_theme_orange);

        } else if (themeName.equals("blue")) {

            themeGroup.check(R.id.rb_theme_blue);

        } else if (themeName.equals("lgreen")) {

            themeGroup.check(R.id.rb_theme_lightgreen);

        } else if (themeName.equals("pink")) {

            themeGroup.check(R.id.rb_theme_pink);

        } else if (themeName.equals("default")) {

            themeGroup.check(R.id.rb_theme_default);

        } else if (themeName.equals("")) {

            themeGroup.check(R.id.rb_theme_default);

        }
    }

    private void setToolBarStyle(String themeName) {
        if (themeName.equals("green")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setBackground(getDrawable(R.drawable.green_title));
            } else
                ContextCompat.getDrawable(this, R.drawable.pink_title);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        } else if (themeName.equals("orange")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setBackground(getDrawable(R.drawable.orange_title));
            } else
                ContextCompat.getDrawable(this, R.drawable.pink_title);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        } else if (themeName.equals("blue")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setBackground(getDrawable(R.drawable.blue_title));
            } else
                ContextCompat.getDrawable(this, R.drawable.pink_title);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        } else if (themeName.equals("lgreen")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setBackground(getDrawable(R.drawable.lightgreen_title));
            } else
                ContextCompat.getDrawable(this, R.drawable.pink_title);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        } else if (themeName.equals("pink")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setBackground(getDrawable(R.drawable.pink_title));

            } else
                ContextCompat.getDrawable(this, R.drawable.pink_title);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        } else if (themeName.equals("default")) {

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        } else if (themeName.equals("")) {

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        }
    }
}
