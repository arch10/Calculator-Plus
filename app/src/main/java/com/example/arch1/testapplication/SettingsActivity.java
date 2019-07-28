package com.example.arch1.testapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.Objects;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class SettingsActivity extends AppCompatActivity {

    private Intent intent;
    private AppPreferences preferences;
    private Dialog precisionDialog;
    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private static final String SURVEY_URL = "https://docs.google.com/forms/d/e/1FAIpQLScjn3uwQpPuDOV_CNEP82HddF59rZw3LRps-IFWE0_b9h2o0w/viewform?usp=sf_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        precisionDialog = new Dialog(this);
        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        int finalColor = color;
        mAdapter = new ListAdapter(this, setListData(), (data, position) -> {
            if (position == 0) {
                intent = new Intent(SettingsActivity.this, GeneralSettingsActivity.class);
                startActivity(intent);
            } else if (position == 1) {
                intent = new Intent(SettingsActivity.this, ThemeActivity.class);
                startActivity(intent);
            } else if (position == 2) {
                //showPopUp
                showPrecisionDialog();
            }else if (position == 3) {
                //Angle
                showAngleDialog();
            }else if (position == 4) {
                //share
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus");
                String msg = "\nHey, checkout this cool Calculator app. It has ";
                msg += "some very nice features. Go to this link to download this app now.\n\n";
                msg += "https://gigaworks.page.link/calculatorplus";
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                startActivity(Intent.createChooser(intent, "Choose one"));
            } else if(position == 5) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setToolbarColor(finalColor)
                        .setShowTitle(true)
                        .build();
                CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent);
                CustomTabsHelper.openCustomTab(this, customTabsIntent, Uri.parse(SURVEY_URL), new WebViewFallback());
            } else if (position == 6) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"arch1824@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus " + BuildConfig.VERSION_NAME
                        + " // " + Build.MANUFACTURER + " " + Build.MODEL + "(" + Build.DEVICE + ")" +
                        " // " + getResources().getDisplayMetrics().densityDpi);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }else if (position == 7) {
                intent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        //setting Recycler View
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

    }

    private void showPrecisionDialog() {
        precisionDialog.setCanceledOnTouchOutside(true);
        precisionDialog.setContentView(R.layout.precision_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        Objects.requireNonNull(precisionDialog.getWindow()).setLayout((int) (width * 0.9), ConstraintLayout.LayoutParams.WRAP_CONTENT);

        Button cancelButton = precisionDialog.findViewById(R.id.buttonCancel);
        Button setButton = precisionDialog.findViewById(R.id.buttonSet);
        final IndicatorSeekBar indicatorSeekBar = precisionDialog.findViewById(R.id.indicatorSeekBar);

        indicatorSeekBar.setProgress(getPrecision());

        cancelButton.setOnClickListener(v -> precisionDialog.dismiss());

        setButton.setOnClickListener(v -> {
            setPrecision(indicatorSeekBar.getProgress());
            precisionDialog.dismiss();
        });
        precisionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        precisionDialog.getWindow().getAttributes().windowAnimations = R.style.WindowTransition;
        precisionDialog.show();
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


        mAdapter.setList(setListData());
        recyclerView.setAdapter(mAdapter);
    }

    private int getPrecision() {
        String precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);

        switch (precision) {
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
            case "six":
                return 6;
            case "seven":
                return 7;
            case "eight":
                return 8;
            case "nine":
                return 9;
            case "ten":
                return 10;
            default:
                return 6;
        }
    }

    private ArrayList<ListData> setListData() {
        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);
        ListData data;

        ArrayList<ListData> list = new ArrayList<>();
        data = new ListData("General", "General user preferences", R.drawable.ic_outline_build_24px);
        list.add(data);
        list.add(Theme.getThemeData(themeName));
        list.add(getPrecisionData());
        data = new ListData("Angle", getAngle(), R.drawable.ic_outline_track_changes_24px);
        list.add(data);
        data = new ListData("Share", "Share this app", R.drawable.ic_outline_share_24px);
        list.add(data);
        data = new ListData("Translate", "Help translate Calculator Plus to you language", R.drawable.ic_translate_black_24dp);
        list.add(data);
        data = new ListData("Report a problem", "Report bug to the developer", R.drawable.ic_outline_feedback_24px);
        list.add(data);
        data = new ListData("About", "Version : " + BuildConfig.VERSION_NAME, R.drawable.ic_outline_info_24px);
        list.add(data);

        return list;
    }

    private ListData getPrecisionData() {
        ListData data = new ListData();
        data.setTitle("Answer Precision");
        data.setImg(R.drawable.ic_outline_create_24px);
        data.setBody("Precision: " + getPrecision());
        return data;
    }

    private String getAngle() {
        boolean ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);

        if (ifDegree) {
            return "Degrees";
        } else {
            return "Radians";
        }
    }

    private void showAngleDialog() {
        precisionDialog.setCanceledOnTouchOutside(true);
        precisionDialog.setContentView(R.layout.angle_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        Objects.requireNonNull(precisionDialog.getWindow()).setLayout((int) (width * 0.9), ConstraintLayout.LayoutParams.WRAP_CONTENT);

        RadioGroup radioGroup = precisionDialog.findViewById(R.id.rg_angle);

        boolean ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);
        if (ifDegree) {
            radioGroup.check(R.id.ang_deg);
        } else {
            radioGroup.check(R.id.ang_rad);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.ang_deg:
                    preferences.setBooleanPreference(AppPreferences.APP_ANGLE, true);
                    break;
                case R.id.ang_rad:
                    preferences.setBooleanPreference(AppPreferences.APP_ANGLE, false);
                    break;
            }
            mAdapter.setList(setListData());
            recyclerView.setAdapter(mAdapter);
            precisionDialog.dismiss();
        });
        precisionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        precisionDialog.getWindow().getAttributes().windowAnimations = R.style.WindowTransition;
        precisionDialog.show();
    }

}
