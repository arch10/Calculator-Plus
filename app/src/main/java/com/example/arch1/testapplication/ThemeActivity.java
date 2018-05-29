package com.example.arch1.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

public class ThemeActivity extends AppCompatActivity {

    private RadioGroup themeGroup;
    private AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

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

        } else if (themeName.equals("")) {

            setTheme(R.style.AppTheme);
            preferences.setStringPreference(AppPreferences.APP_THEME, "orange");

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

        } else if (themeName.equals("")) {

            themeGroup.check(R.id.rb_theme_orange);

        }
    }
}
