package com.example.arch1.testapplication;

import android.content.Intent;
import android.content.res.TypedArray;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
    private ImageView noHistoryIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noHistoryLayout = findViewById(R.id.fl_no_history);
        noHistoryIcon = findViewById(R.id.iv_icon);

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();

        switch (themeName) {
            case Theme.DEFAULT:
                color = getResources().getColor(R.color.colorMaterialSteelGrey);
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case Theme.MATERIAL_LIGHT:
                toolbar.setTitleTextColor(getResources().getColor(R.color.gray));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                break;
            case Theme.MATERIAL_DARK:
                noHistoryIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite));
            default:
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

        history = new History(this);
        recyclerView = findViewById(R.id.rv_history);
        mLayoutManager = new LinearLayoutManager(this);


        mAdapter = new HistoryAdapter(this, reverseHistory(history.showHistory()), new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(Calculations data, int position) {
                //History Clicked
                preferences.setBooleanPreference(AppPreferences.APP_HISTORY_SET, true);
                preferences.setStringPreference(AppPreferences.APP_HISTORY_EQUATION, data.getEquation());
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

        if (mAdapter.getItemCount() == 0) {
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
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noHistoryLayout.setVisibility(View.VISIBLE);

            if (delItem != null) {
                delItem.setVisible(false);
            }
        }
    }
}
