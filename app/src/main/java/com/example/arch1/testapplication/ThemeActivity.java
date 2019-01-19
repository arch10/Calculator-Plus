package com.example.arch1.testapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);
        if(themeName.equals("default") || themeName.equals(""))
            color = getResources().getColor(R.color.colorMaterialSteelGrey);
        if(themeName.equals("material"))
            color = getResources().getColor(R.color.colorMaterialDarkBlue);

        //setting toolbar style manually
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setBackgroundColor(color);

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
                    case R.id.rb_theme_red:
                        changeTheme("red");
                        break;
                    case R.id.rb_theme_lightgreen:
                        changeTheme("lgreen");
                        break;
                    case R.id.rb_theme_pink:
                        changeTheme("pink");
                        break;
                    case R.id.rb_theme_purple:
                        changeTheme("purple");
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
        } else if (themeName.equals("orange")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "orange");
        } else if (themeName.equals("blue")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "blue");
        } else if(themeName.equals("red")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "red");
        } else if (themeName.equals("lgreen")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "lgreen");
        } else if (themeName.equals("pink")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "pink");
        } else if (themeName.equals("purple")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "purple");
        } else if (themeName.equals("default")) {
            preferences.setStringPreference(AppPreferences.APP_THEME, "default");
        }

        Intent intent[] = new Intent[3];
        intent[2] = new Intent(this, ThemeActivity.class);
        intent[1] = new Intent(this, SettingsActivity.class);
        intent[0] = new Intent(this, MainActivity.class);
        startActivities(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();

    }

    private void setTheme(String themeName) {
        if (themeName.equals("green")) {

            setTheme(R.style.GreenAppTheme);

        } else if (themeName.equals("orange")) {

            setTheme(R.style.AppTheme);

        } else if (themeName.equals("blue")) {

            setTheme(R.style.BlueAppTheme);

        } else if(themeName.equals("red")) {

            setTheme(R.style.RedAppTheme);

        }else if (themeName.equals("lgreen")) {

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

    private void checkSelectedTheme() {
        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        if (themeName.equals("green")) {

            themeGroup.check(R.id.rb_theme_green);

        } else if (themeName.equals("orange")) {

            themeGroup.check(R.id.rb_theme_orange);

        } else if (themeName.equals("blue")) {

            themeGroup.check(R.id.rb_theme_blue);

        } else if(themeName.equals("red")) {

            themeGroup.check(R.id.rb_theme_red);

        } else if (themeName.equals("lgreen")) {

            themeGroup.check(R.id.rb_theme_lightgreen);

        } else if (themeName.equals("pink")) {

            themeGroup.check(R.id.rb_theme_pink);

        } else if (themeName.equals("purple")) {

            themeGroup.check(R.id.rb_theme_purple);

        } else if (themeName.equals("default")) {

            themeGroup.check(R.id.rb_theme_default);

        } else if (themeName.equals("")) {

            themeGroup.check(R.id.rb_theme_default);

        }
    }

}
