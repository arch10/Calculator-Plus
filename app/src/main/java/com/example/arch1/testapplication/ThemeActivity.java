package com.example.arch1.testapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ThemeActivity extends AppCompatActivity {

    private RadioGroup themeGroup;
    private AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);
        a.recycle();

        if(themeName.equals(Theme.DEFAULT)) {
            color = getResources().getColor(R.color.colorMaterialSteelGrey);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if(themeName.equals(Theme.MATERIAL_LIGHT)) {
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

        themeGroup = findViewById(R.id.rg_theme_group);
        checkSelectedTheme();

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_theme_green:
                        changeTheme(Theme.GREEN);
                        break;
                    case R.id.rb_theme_orange:
                        changeTheme(Theme.ORANGE);
                        break;
                    case R.id.rb_theme_blue:
                        changeTheme(Theme.BLUE);
                        break;
                    case R.id.rb_theme_red:
                        changeTheme(Theme.RED);
                        break;
                    case R.id.rb_theme_lightgreen:
                        changeTheme(Theme.LIGHT_GREEN);
                        break;
                    case R.id.rb_theme_pink:
                        changeTheme(Theme.PINK);
                        break;
                    case R.id.rb_theme_purple:
                        changeTheme(Theme.PURPLE);
                        break;
                    case R.id.rb_theme_material2:
                        changeTheme(Theme.MATERIAL_LIGHT);
                        break;
                    case R.id.rb_theme_material_dark:
                        changeTheme(Theme.MATERIAL_DARK);
                        break;
                    case R.id.rb_theme_default:
                        changeTheme(Theme.DEFAULT);
                }
            }
        });

    }

    private void changeTheme(String themeName) {
        Theme.changeTheme(themeName, preferences);
        Toast.makeText(this, preferences.getStringPreference(AppPreferences.APP_THEME), Toast.LENGTH_SHORT).show();
        Intent intent[] = new Intent[3];
        intent[2] = new Intent(this, ThemeActivity.class);
        intent[1] = new Intent(this, SettingsActivity.class);
        intent[0] = new Intent(this, MainActivity.class);
        startActivities(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private void checkSelectedTheme() {
        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        switch (themeName) {
            case Theme.GREEN :
                themeGroup.check(R.id.rb_theme_green);
                break;
            case Theme.ORANGE :
                themeGroup.check(R.id.rb_theme_orange);
                break;
            case Theme.BLUE :
                themeGroup.check(R.id.rb_theme_blue);
                break;
            case Theme.RED :
                themeGroup.check(R.id.rb_theme_red);
                break;
            case Theme.LIGHT_GREEN :
                themeGroup.check(R.id.rb_theme_lightgreen);
                break;
            case Theme.PINK :
                themeGroup.check(R.id.rb_theme_pink);
                break;
            case Theme.PURPLE :
                themeGroup.check(R.id.rb_theme_purple);
                break;
            case Theme.MATERIAL_LIGHT :
                themeGroup.check(R.id.rb_theme_material2);
                break;
            case Theme.MATERIAL_DARK :
                themeGroup.check(R.id.rb_theme_material_dark);
                break;
            case Theme.DEFAULT :
                themeGroup.check(R.id.rb_theme_default);
            default :
                themeGroup.check(R.id.rb_theme_material2);
        }
    }

}
