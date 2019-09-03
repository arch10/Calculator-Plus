package com.example.arch1.testapplication;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

class History {

    private String jsonString;
    private Context context;
    private AppPreferences preferences;

    History(Context context) {
        this.context = context;
        preferences = AppPreferences.getInstance(context);
        jsonString = preferences.getStringPreference(AppPreferences.APP_HISTORY);
    }

    void setJsonString(String jsonString) {
        this.jsonString = jsonString;
        preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
    }

    void addToHistory(String title, String body, Long date) {
        jsonString = preferences.getStringPreference(AppPreferences.APP_HISTORY);
        JSONArray array;
        if (jsonString.equals("")) {
            array = new JSONArray();
        } else {

            if (jsonString.contains("\"title\":\"" + title + "\"")) {
                String js = jsonString;
                try {
                    updateHistory(title);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonString = js;
                    preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
                }
                return;
            }
            try {
                array = new JSONArray(jsonString);
            } catch (JSONException e) {
                array = new JSONArray();
                e.printStackTrace();
            }
        }

        String dateString = date + "";

        JSONObject object = new JSONObject();
        try {
            object.put("title", title);
            object.put("body", body);
            object.put("date", dateString);
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonString = array.toString();

        preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
    }

    ArrayList<Calculations> showHistory() {
        jsonString = preferences.getStringPreference(AppPreferences.APP_HISTORY);

        ArrayList<Calculations> calcArray = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Date date = new Date(Long.parseLong(object.getString("date")));
                boolean isToday = DateUtils.isToday(Long.parseLong(object.getString("date")));
                boolean isYesterday = isYesterday(Long.parseLong(object.getString("date")));
                String dateString;
                if (isToday)
                    dateString = context.getString(R.string.today);
                else if (isYesterday)
                    dateString = context.getString(R.string.yesterday);
                else
                    dateString = getDateInstance().format(date);

                Calculations calc = new Calculations(object.getString("title"), object.getString("body"), dateString);
                calcArray.add(calc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return calcArray;
    }

    private boolean isYesterday(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay - 1);
    }

    private void updateHistory(String title) throws JSONException {
        JSONObject testObj = null;
        JSONArray ary = new JSONArray();
        boolean found = false;

        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.getString("title").equals(title)) {
                found = true;
                testObj = object;
                continue;
            }
            ary.put(object);
        }

        if (found)
            testObj.put("date", System.currentTimeMillis());

        ary.put(testObj);
        jsonString = ary.toString();
        preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
    }

    public void deleteHistory(String title) {
        jsonString = preferences.getStringPreference(AppPreferences.APP_HISTORY);

        if (jsonString.contains("\"title\":\"" + title + "\"")) {
            String js = jsonString;
            try {
                delete(title);
            } catch (JSONException e) {
                e.printStackTrace();
                jsonString = js;
                preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
            }
        }
    }

    private void delete(String title) throws JSONException {
        JSONArray ary = new JSONArray();
        new JSONArray(new JSONArray());

        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.getString("title").equals(title)) {
                continue;
            }
            ary.put(object);
        }

        jsonString = ary.toString();
        preferences.setStringPreference(AppPreferences.APP_HISTORY, jsonString);
    }

}
