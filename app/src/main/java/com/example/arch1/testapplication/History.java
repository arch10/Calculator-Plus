package com.example.arch1.testapplication;

import android.content.Context;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History {

    private String jsonString;
    private AppPreferences preferences;
    private Context context;

    public History (Context context){
        this.context = context;
        preferences = AppPreferences.getInstance(context);
        jsonString = preferences.getStringPreference(AppPreferences.APP_HISTORY);
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
        preferences.setStringPreference(AppPreferences.APP_HISTORY,jsonString);
    }

    public void addToHistory(String title, String body, Long date){
        JSONArray array;
        if(jsonString.equals("")){
            array = new JSONArray();
        } else {
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
            object.put("title",title);
            object.put("body",body);
            object.put("date",dateString);
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonString = array.toString();

        preferences.setStringPreference(AppPreferences.APP_HISTORY,jsonString);
    }

    public ArrayList<Calculations> showHistory(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList<Calculations> calcArray = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonString);
            for(int i=0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                Date date = new Date(Long.parseLong(object.getString("date")));
                boolean isToday = DateUtils.isToday(Long.parseLong(object.getString("date")));
                String dateString;
                if(isToday)
                    dateString = "Today";
                else
                    dateString = sdf.format(date);

                Calculations calc = new Calculations(object.getString("title"),object.getString("body"),dateString);
                calcArray.add(calc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return calcArray;
    }

    public ArrayList<Calculations> removeHistory(String removeDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String jsonTemp = "";
        ArrayList<Calculations> calcArray = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonString);
            for(int i=0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                String currentDate = object.getString("date");
                if(currentDate.equals(removeDate)){
                    continue;
                }
                Date date = new Date(Long.parseLong(currentDate));
                boolean isToday = DateUtils.isToday(Long.parseLong(object.getString("date")));
                String dateString;
                if(isToday)
                    dateString = "Today";
                else
                    dateString = sdf.format(date);

                Calculations calc = new Calculations(object.getString("title"),object.getString("body"),dateString);
                calcArray.add(calc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return calcArray;
    }

}
