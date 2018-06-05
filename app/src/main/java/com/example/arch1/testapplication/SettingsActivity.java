package com.example.arch1.testapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfSettings;
    private Intent intent;
    private AppPreferences preferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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


        listView = findViewById(R.id.listview);

        listOfSettings = new ArrayList<>();
        listOfSettings.add("Themes");

        arrayAdapter = new ArrayAdapter<>(this, R.layout.setting_list_layout, listOfSettings);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    intent = new Intent(SettingsActivity.this, ThemeActivity.class);
                    startActivity(intent);
                }
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
