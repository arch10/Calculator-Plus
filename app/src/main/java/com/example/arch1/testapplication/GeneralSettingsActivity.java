package com.example.arch1.testapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class GeneralSettingsActivity extends AppCompatActivity {

    private AppPreferences preferences;
    private Toolbar toolbar;
    private Switch numberFormatterSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

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

        numberFormatterSwitch = findViewById(R.id.switch1);


        //setting preference values
        numberFormatterSwitch.setChecked(preferences.getBooleanPreference(AppPreferences.APP_NUMBER_FORMATTER));


        numberFormatterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.setBooleanPreference(AppPreferences.APP_NUMBER_FORMATTER,isChecked);
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

    private void setToolBarStyle(String themeName) {
        if (themeName.equals("green")) {
            toolbar.setBackground(getDrawable(R.drawable.green_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("orange")) {
            toolbar.setBackground(getDrawable(R.drawable.orange_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("blue")) {
            toolbar.setBackground(getDrawable(R.drawable.blue_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("lgreen")) {
            toolbar.setBackground(getDrawable(R.drawable.lightgreen_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("pink")) {
            toolbar.setBackground(getDrawable(R.drawable.pink_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("default")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorMaterialSteelGrey));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else if (themeName.equals("")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorMaterialSteelGrey));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
    }
}
