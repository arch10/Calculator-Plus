package com.example.arch1.testapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private HistoryAdapter mAdapter;
    private AppPreferences preferences;
    private Toolbar toolbar;
    private History history;
    private FrameLayout noHistoryLayout;
    private MenuItem delItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);
        if(themeName.equals("default") || themeName.equals(""))
            color = getResources().getColor(R.color.colorMaterialSteelGrey);


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

        noHistoryLayout = findViewById(R.id.fl_no_history);
        history = new History(this);
        recyclerView = findViewById(R.id.rv_history);
        mLayoutManager = new LinearLayoutManager(this);


        mAdapter = new HistoryAdapter(this, reverseHistory(history.showHistory()), new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(Calculations data, int position) {
                //History Clicked
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("equation", data.getEquation());
                startActivity(intent);
                finish();
            }
        }, new HistoryAdapter.OnHistoryLongPressListener() {
            @Override
            public void onHistoryLongPressed(Calculations data, int position) {
                //long Pressed
            }
        });

        //setting Recycler View
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        checkHistoryStatus();
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

        } else if (themeName.equals("material")) {

            setTheme(R.style.Material2);

        } else if (themeName.equals("default")) {

            setTheme(R.style.DefAppTheme);

        } else if (themeName.equals("")) {

            setTheme(R.style.DefAppTheme);
            preferences.setStringPreference(AppPreferences.APP_THEME, "default");

        }
    }


    private ArrayList<Calculations> reverseHistory(ArrayList<Calculations> calculations) {
        ArrayList<Calculations> list = new ArrayList<>();

        for (int i = calculations.size() - 1; i >= 0; i--) {
            list.add(calculations.get(i));
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        delItem = menu.getItem(0);

        if(mAdapter.getItemCount() == 0){
            menu.getItem(0).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clearHistory) {
            history.setJsonString("");
            mAdapter.setList(reverseHistory(history.showHistory()));
            recyclerView.setAdapter(mAdapter);
            checkHistoryStatus();
        }
        return true;
    }

    private void checkHistoryStatus() {
        if(mAdapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            noHistoryLayout.setVisibility(View.VISIBLE);

            if(delItem!=null){
                delItem.setVisible(false);
            }
        }
    }
}
