package com.example.arch1.testapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.warkiz.widget.IndicatorSeekBar;

public class Precision extends AppCompatActivity {

    private AppPreferences preferences;
    private Button setButton, cancelButton;
    private IndicatorSeekBar indicatorSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision);

        setButton = findViewById(R.id.buttonSet);
        cancelButton = findViewById(R.id.buttonCancel);
        indicatorSeekBar = findViewById(R.id.indicatorSeekBar);

        indicatorSeekBar.setProgress(getPrecision());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.5));

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrecision(indicatorSeekBar.getProgress());
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void setPrecision(int precision) {
        switch (precision) {
            case 2:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "two");
                break;
            case 3:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "three");
                break;
            case 4:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "four");
                break;
            case 5:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "five");
                break;
            case 6:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
                break;
            case 7:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "seven");
                break;
            case 8:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "eight");
                break;
            case 9:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "nine");
                break;
            case 10:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "ten");
                break;
            default:
                preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
                break;
        }
    }

    private int getPrecision() {
        String precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);

        switch (precision) {
            case "two": return 2;
            case "three": return 3;
            case "four": return 4;
            case "five": return 5;
            case "six": return 6;
            case "seven": return 7;
            case "eight": return 8;
            case "nine": return 9;
            case "ten": return 10;
            default: return 6;
        }
    }
}
