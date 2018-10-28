package com.example.arch1.testapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private Intent intent;
    private AppPreferences preferences;
    private Toolbar toolbar;
    private Dialog precisionDialog;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);
        if(themeName.equals("default") || themeName.equals(""))
            color = getResources().getColor(R.color.colorMaterialSteelGrey);
        a.recycle();

        //setting toolbar style manually
        //setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setBackgroundColor(color);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        precisionDialog = new Dialog(this);
        recyclerView = findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new ListAdapter(this, setListData(), new ListAdapter.OnSettingClickListener() {
            @Override
            public void OnSettingClick(ListData data, int position) {
                if (position == 0) {
                    intent = new Intent(SettingsActivity.this, GeneralSettingsActivity.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    intent = new Intent(SettingsActivity.this, ThemeActivity.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    //showPopUp
                    showPrecisionDialog();
                }
                if (position == 3) {
                    //Angle
                    showAngleDialog();
                }
                if (position == 4) {
                    //share
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus");
                    String msg = "\nHey, checkout this cool Calculator app. It has ";
                    msg += "some very cool features. Go to this link to download this app now.\n\n";
                    msg += "https://play.google.com/store/apps/details?id=com.gigaworks.tech.calculator";
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, "Choose one"));
                }
                if (position == 5) {
                    intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/email");
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"arch1824@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus "+BuildConfig.VERSION_NAME
                    +" // "+Build.MANUFACTURER+" "+Build.MODEL +"("+Build.DEVICE+")"+
                    " // " + getResources().getDisplayMetrics().densityDpi);
                    startActivity(intent);
                }
                if (position == 6) {
                    intent = new Intent(SettingsActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
            }
        });

        //setting Recycler View
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

    }

    private void setTheme(String themeName) {
        if (themeName.equals("green")) {

            setTheme(R.style.GreenAppTheme);

        } else if (themeName.equals("orange")) {

            setTheme(R.style.AppTheme);

        } else if (themeName.equals("blue")) {

            setTheme(R.style.BlueAppTheme);

        } else if (themeName.equals("red")) {

            setTheme(R.style.RedAppTheme);

        } else if (themeName.equals("lgreen")) {

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

    private void showPrecisionDialog() {
        precisionDialog.setCanceledOnTouchOutside(true);
        precisionDialog.setContentView(R.layout.precision_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        precisionDialog.getWindow().setLayout((int) (width * 0.9), ConstraintLayout.LayoutParams.WRAP_CONTENT);

        Button cancelButton = precisionDialog.findViewById(R.id.buttonCancel);
        Button setButton = precisionDialog.findViewById(R.id.buttonSet);
        final IndicatorSeekBar indicatorSeekBar = precisionDialog.findViewById(R.id.indicatorSeekBar);

        indicatorSeekBar.setProgress(getPrecision());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                precisionDialog.dismiss();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrecision(indicatorSeekBar.getProgress());
                precisionDialog.dismiss();
            }
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
        data = new ListData("General","General user preferences",R.drawable.ic_outline_build_24px);
        list.add(data);
        list.add(getThemeData(themeName));
        list.add(getPrecisionData());
        data = new ListData("Angle", getAngle(), R.drawable.ic_outline_track_changes_24px);
        list.add(data);
        data = new ListData("Share", "Share this app", R.drawable.ic_outline_share_24px);
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

    private ListData getThemeData(String themeName) {
        ListData data = new ListData();
        data.setTitle("Themes");
        data.setImg(R.drawable.ic_outline_color_lens_24px);

        if (themeName.equals("green")) {

            data.setBody("Green");

        } else if (themeName.equals("orange")) {

            data.setBody("Orange");

        } else if (themeName.equals("blue")) {

            data.setBody("Blue");

        } else if (themeName.equals("red")) {

            data.setBody("Red");

        } else if (themeName.equals("lgreen")) {

            data.setBody("Light Green");

        } else if (themeName.equals("pink")) {

            data.setBody("Pink");

        }  else if (themeName.equals("purple")) {

            data.setBody("Purple");

        } else if (themeName.equals("default")) {

            data.setBody("Classic");

        } else if (themeName.equals("")) {

            data.setBody("Classic");
            preferences.setStringPreference(AppPreferences.APP_THEME, "default");

        }

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
        precisionDialog.getWindow().setLayout((int) (width * 0.9), ConstraintLayout.LayoutParams.WRAP_CONTENT);

        RadioGroup radioGroup = precisionDialog.findViewById(R.id.rg_angle);

        boolean ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);
        if (ifDegree) {
            radioGroup.check(R.id.ang_deg);
        } else {
            radioGroup.check(R.id.ang_rad);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });
        precisionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        precisionDialog.getWindow().getAttributes().windowAnimations = R.style.WindowTransition;
        precisionDialog.show();
    }

}
