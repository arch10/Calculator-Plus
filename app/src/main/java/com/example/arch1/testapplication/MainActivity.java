package com.example.arch1.testapplication;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity{

    private TextView text;
    private Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20;
    private float firstNum=0,secondNum=0;
    private int operator=-1;
    private boolean ifEqual=false;
    private EditText display1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display1 = findViewById(R.id.et_display1);
        display1.setShowSoftInputOnFocus(false);
        display1.setTextIsSelectable(false);
        display1.setLongClickable(false);

    }

}
