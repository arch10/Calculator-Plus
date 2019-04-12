package com.example.arch1.testapplication;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.Stack;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private TextView result;
    private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, badd, bsub, bmul, bdiv, bequal, bdel, bdecimal;
    private Button sin, cos, tan, asin, acos, atan, exp, log, ln, pow, factorial, sqrt, cbrt, pi;
    private Button open, close, percent, ms, mr, mPlus, mMinus;
    private EditText equation;
    private String equ = "", tempEqu, tempResult = "";
    private View view;
    private Animator anim;
    private View mainLayout, slidingLayout;
    private AppPreferences preferences;
    private androidx.appcompat.widget.Toolbar toolbar;
    private boolean firstLaunch;
    private Menu menu;
    private boolean ifDegree, enableNumberFormatter, enableSmartCalculation = false;
    private History history;
    private ViewPager mPadViewPager;

    private static final String mulSymbol = "\u00d7";
    private static final String piSymbol = "\u03c0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set Activity Theme
        preferences = AppPreferences.getInstance(this);
        setTheme(Theme.getTheme(preferences.getStringPreference(AppPreferences.APP_THEME)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting values from saved Instance, if any
        if (savedInstanceState != null) {
            equ = savedInstanceState.getString("equ");
            ifDegree = savedInstanceState.getBoolean("ifDeg");
        }

        //setting menu for DEG or RAD
        ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);

        //initialising variables
        initialiseVariables();

        //checking if first Launch
        firstLaunch = preferences.getBooleanPreference(AppPreferences.APP_FIRST_LAUNCH);
        if (firstLaunch) {
            //set app default preferences
            preferences.setBooleanPreference(AppPreferences.APP_FIRST_LAUNCH, false);
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
            preferences.setStringPreference(AppPreferences.APP_THEME, Theme.MATERIAL_LIGHT);
            preferences.setBooleanPreference(AppPreferences.APP_ANGLE, true);
            preferences.setBooleanPreference(AppPreferences.APP_NUMBER_FORMATTER, true);
            preferences.setBooleanPreference(AppPreferences.APP_SMART_CALCULATIONS, true);
            preferences.setStringPreference(AppPreferences.APP_HISTORY, "");
            preferences.setStringPreference(AppPreferences.APP_EQUATION_STRING, "");
        }

        //getting primary color of the theme
        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();

        //setting toolbar manually
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(color);

        int orientation = getResources().getConfiguration().orientation;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE ||
                metrics.densityDpi >= DisplayMetrics.DENSITY_560) {
            FrameLayout arrowLayout = slidingLayout.findViewById(R.id.fl_arrow);
            arrowLayout.setVisibility(View.GONE);
        }

        //avoiding keyboard input
        equation.setShowSoftInputOnFocus(false);
        equation.setTextIsSelectable(false);
        equation.setLongClickable(false);

        //adding button long press listener
        bdel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!equation.getText().toString().equals(""))
                    animateClear(view);
                equ = "";
                equation.setText(equ);
                result.setText("");
                return true;
            }
        });

        //adding text change listener
        equation.addTextChangedListener(this);

    }

    private boolean isAnError(String string) {
        if (string.equals("Invalid Expression") ||
                string.equals("Domain error") ||
                string.equals("Cannot divide by 0") ||
                string.equals("Number too large"))
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        char c;

        switch (id) {
            case R.id.mul:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%' || c == '!' || c == ')' || isNumber(c)) {
                        add("\u00d7");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeBackOperators();
                        add("\u00d7");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7");
                        break;
                    }
                }
                break;

            case R.id.div:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%' || c == '!' || c == ')' || isNumber(c)) {
                        add("÷");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeBackOperators();
                        add("÷");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("÷");
                        break;
                    }
                }
                break;

            case R.id.equal:
                if (!isEquationEmpty()) {
                    String res = result.getText().toString().trim();
                    if (res.equals("") || isAnError(res)) {
                        result.setText(Evaluate.errMsg);
                        if (preferences.getStringPreference(AppPreferences.APP_THEME).equals("red")) {
                            result.setTextColor(getResources().getColor(R.color.colorMaterialYellow));
                        } else {
                            result.setTextColor(getResources().getColor(R.color.colorRed));
                        }
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        result.startAnimation(shake);
                        break;
                    }
                    if (!res.equals("")) {
                        String historyEqu = equ;
                        String historyVal = res;
                        history.addToHistory(historyEqu, historyVal, System.currentTimeMillis());
                        tempResult = res;
                        equ = "";
                        equation.setText(res);
                        result.setText("");
                    }
                }
                break;

            case R.id.del:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '(') {
                        if (equ.length() >= 3) {
                            c = equ.charAt(equ.length() - 2);
                            if (isAlphabet(c)) {
                                removeTrigo();
                                equ = equ.replace(",", "");
                                if (enableNumberFormatter)
                                    equ = formatEquation(equ);
                                equation.setText(equ);
                                break;
                            }
                        }
                    }
                    equ = equ.substring(0, equ.length() - 1);
                    equ = equ.replace(",", "");
                    if (enableNumberFormatter)
                        equ = formatEquation(equ);
                    equation.setText(equ);
                } else {
                    tempResult = "";
                    equation.setText("");
                }
                break;

            case R.id.decimal:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == 'e' || c == '\u03c0')
                        break;
                    if (isNumber(c) && canPlaceDecimal()) {
                        add(".");
                        break;
                    }
                    if (c == ')' || c == '%' || c == '!') {
                        add("\u00d70.");
                        break;
                    }
                    if (isOperator(c) || c == '(' || c == '\u221a' || c == '\u221b') {
                        add("0.");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        if (canPlaceDecimal()) {
                            add(".");
                            break;
                        }
                        break;
                    }
                    add("0.");
                }
                break;

            case R.id.add:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%' || c == '!' || c == ')') {
                        add("+");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeBackOperators();
                        add("+");
                        break;
                    }
                    if (isNumber(c)) {
                        add("+");
                    }
                    break;
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("+");
                        break;
                    }
                }
                break;

            case R.id.sub:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%' || c == ')' || c == '!' || c == '(') {
                        add("-");
                        break;
                    }

                    if (isOperator(c)) {
                        if (equ.length() >= 2 && (isNumber(equ.charAt(equ.length() - 2)))) {
                            if (c == '-') {
                                removeBackOperators();
                                add("+");
                                break;
                            }
                            add("-");
                            break;
                        }
                        removeBackOperators();
                        add("-");
                        break;
                    }

                    if (isNumber(c)) {
                        add("-");
                        break;
                    }
                    break;
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("-");
                        break;
                    }
                }
                if (!ifPrevOperator() || equ.equals("")) {
                    add("-");
                } else if (ifPrevOperator()) {
                    if (!isEquationEmpty()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("-");
                    }
                }
                break;

            case R.id.one:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d71");
                    break;
                }
                add("1");
                break;

            case R.id.two:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d72");
                    break;
                }
                add("2");
                break;

            case R.id.three:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d73");
                    break;
                }
                add("3");
                break;

            case R.id.four:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d74");
                    break;
                }
                add("4");
                break;

            case R.id.five:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d75");
                    break;
                }
                add("5");
                break;

            case R.id.six:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d76");
                    break;
                }
                add("6");
                break;

            case R.id.seven:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d77");
                    break;
                }
                add("7");
                break;

            case R.id.eight:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d78");
                    break;
                }
                add("8");
                break;

            case R.id.nine:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d79");
                    break;
                }
                add("9");
                break;

            case R.id.zero:
                tempResult = "";
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d70");
                    break;
                }
                add("0");
                break;

            case R.id.percent:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == '!') {
                        equ = equ.substring(0, equ.length() - 1);
                        add("%");
                        break;
                    }

                    if (isNumber(c) || c == ')') {
                        add("%");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeAllBackOperators();
                        if (!isEquationEmpty())
                            add("%");
                        break;
                    }
                    if (c == '.') {
                        add("0%");
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("%");
                        break;
                    }
                }
                break;

            case R.id.open:
                if (!isEquationEmpty() && equ.charAt(equ.length() - 1) == '.') {
                    equ = equ.substring(0, equ.length() - 1);
                    add("\u00d7(");
                    break;
                }

                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == '!') {
                        add("\u00d7(");
                        break;
                    }
                    if (c == '(' || isOperator(c) || c == '\u221a' || c == '\u221b') {
                        add("(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7(");
                        break;
                    }
                    add("(");
                }
                break;

            case R.id.close:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == 'e' || c == '\u03c0' || c == '!') {
                        add(")");
                        break;
                    }
                    if (isNumber(c) || c == ')' || c == '%') {
                        add(")");
                        break;
                    }
                    if (c == '.') {
                        add("0)");
                        break;
                    }
                    if (c == '(') {
                        if (equ.length() >= 3) {
                            c = equ.charAt(equ.length() - 2);
                            if (isAlphabet(c)) {
                                break;
                            }
                            equ = equ.substring(0, equ.length() - 1);
                            equation.setText(equ);
                            break;
                        }
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add(")");
                        break;
                    }
                }
                break;

            case R.id.sin:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7sin(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7sin(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7sin(");
                        break;
                    }
                }
                add("sin(");
                break;
            case R.id.cos:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7cos(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7cos(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7cos(");
                        break;
                    }
                }
                add("cos(");
                break;
            case R.id.tan:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7tan(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7tan(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7tan(");
                        break;
                    }
                }
                add("tan(");
                break;
            case R.id.asin:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7asin(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7asin(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7asin(");
                        break;
                    }
                }
                add("asin(");
                break;
            case R.id.acos:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7acos(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7acos(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7acos(");
                        break;
                    }
                }
                add("acos(");
                break;
            case R.id.atan:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7atan(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7atan(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7atan(");
                        break;
                    }
                }
                add("atan(");
                break;

            case R.id.exp:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == ')' || isNumber(c) || c == '!') {
                        add("\u00d7e");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7e");
                        break;
                    }
                    if (c == '(' || c == '\u00d7' || c == '+' || c == '-' || c == '÷' || c == '^' || c == '\u221a' || c == '\u221b') {
                        add("e");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7e");
                        break;
                    }
                    add("e");
                    break;
                }
                break;
            case R.id.log:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7log(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7log(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7log(");
                        break;
                    }
                }
                add("log(");
                break;
            case R.id.ln:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("\u00d7ln(");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7ln(");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7ln(");
                        break;
                    }
                }
                add("ln(");
                break;
            case R.id.pow:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c) || c == '%' || c == ')' || c == 'e' || c == '!' || c == '\u03c0') {
                        add("^");
                        break;
                    }
                    if (c == '.') {
                        add("0^");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeBackOperators();
                        add("^");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("^");
                        break;
                    }
                }
                break;
            case R.id.fact:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == '!') {
                        equ = equ.substring(0, equ.length() - 1);
                        add("!");
                        break;
                    }

                    if (isNumber(c) || c == ')') {
                        add("!");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeAllBackOperators();
                        if (!isEquationEmpty())
                            add("!");
                        break;
                    }
                    if (c == '.') {
                        add("0!");
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("!");
                        break;
                    }
                }
                break;
            case R.id.sqroot:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == ')' || isNumber(c) || c == '!') {
                        add("\u00d7\u221a");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7\u221a");
                        break;
                    }
                    if (c == '(' || c == '\u00d7' || c == '+' || c == '-' || c == '÷' || c == '^' || c == '\u221a' || c == '\u221b') {
                        add("\u221a");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7\u221a");
                        break;
                    }
                    add("\u221a");
                    break;
                }
                break;
            case R.id.cuberoot:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == ')' || isNumber(c) || c == '!') {
                        add("\u00d7\u221b");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7\u221b");
                        break;
                    }
                    if (c == '(' || c == '\u00d7' || c == '+' || c == '-' || c == '÷' || c == '^' || c == '\u221a' || c == '\u221b') {
                        add("\u221b");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7\u221b");
                        break;
                    }
                    add("\u221b");
                    break;
                }
                break;
            case R.id.pi:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);

                    if (c == '%' || c == ')' || isNumber(c) || c == '!') {
                        add("\u00d7\u03c0");
                        break;
                    }
                    if (c == '.') {
                        add("0\u00d7\u03c0");
                        break;
                    }
                    if (c == '(' || c == '\u00d7' || c == '+' || c == '-' || c == '÷' || c == '^' || c == '\u221a' || c == '\u221b') {
                        add("\u03c0");
                        break;
                    }
                } else {
                    if (!tempResult.equals("")) {
                        equ = tempResult;
                        tempResult = "";
                        add("\u00d7\u03c0");
                        break;
                    }
                    add("\u03c0");
                    break;
                }
                break;

            case R.id.ms:
                if (!result.getText().toString().isEmpty()) {
                    preferences.setStringPreference(AppPreferences.APP_MEMORY_VALUE, result.getText().toString());
                }
                break;
            case R.id.mr:
                String memory = preferences.getStringPreference(AppPreferences.APP_MEMORY_VALUE);
                if(!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if(equ.endsWith("%") || equ.endsWith(")") || equ.endsWith("e") || equ.endsWith("!") || equ.endsWith(piSymbol)) {
                        add(mulSymbol + memory);
                        break;
                    }
                    if(!isNumber(c)) {
                        add(memory);
                        break;
                    }
                } else {
                    add(memory);
                }
                break;
            case R.id.mplus:
                if (!result.getText().toString().isEmpty()) {
                    if(!preferences.getStringPreference(AppPreferences.APP_MEMORY_VALUE).equals("")) {
                        Double init = Double.parseDouble(preferences.getStringPreference(AppPreferences.APP_MEMORY_VALUE));
                        Double res = Double.parseDouble(result.getText().toString());
                        res = init + res;
                        preferences.setStringPreference(AppPreferences.APP_MEMORY_VALUE, Evaluate.roundMyAnswer(res.toString()));
                    }
                }
                break;
            case R.id.mminus:
                if (!result.getText().toString().isEmpty()) {
                    if(!preferences.getStringPreference(AppPreferences.APP_MEMORY_VALUE).equals("")) {
                        Double init = Double.parseDouble(preferences.getStringPreference(AppPreferences.APP_MEMORY_VALUE));
                        Double res = Double.parseDouble(result.getText().toString());
                        res = init - res;
                        preferences.setStringPreference(AppPreferences.APP_MEMORY_VALUE, Evaluate.roundMyAnswer(res.toString()));
                    }
                }
                break;

        }
    }

    private boolean testNumber() {
        return (equ.charAt(equ.length() - 1) == ')'
                || equ.charAt(equ.length() - 1) == '%'
                || equ.charAt(equ.length() - 1) == '!'
                || equ.charAt(equ.length() - 1) == 'e'
                || equ.charAt(equ.length() - 1) == '\u03c0'
        );
    }

    private void removeBackOperators() {
        if (!isEquationEmpty()) {
            char c = equ.charAt(equ.length() - 1);
            while (isOperator(c) && !(c == '%' || c == '!')) {
                equ = equ.substring(0, equ.length() - 1);
                if (equ.length() == 0)
                    break;
                c = equ.charAt(equ.length() - 1);
            }
        }
        equation.setText(equ);
    }

    private void removeAllBackOperators() {
        if (!isEquationEmpty()) {
            char c = equ.charAt(equ.length() - 1);
            while (isOperator(c)) {
                equ = equ.substring(0, equ.length() - 1);
                if (equ.length() == 0)
                    break;
                c = equ.charAt(equ.length() - 1);
            }
        }
        equation.setText(equ);
    }

    private void removeTrigo() {
        equ = equ.substring(0, equ.length() - 1);

        while (!equ.isEmpty()) {
            char c = equ.charAt(equ.length() - 1);
            if (isAlphabet(c)) {
                equ = equ.substring(0, equ.length() - 1);
            } else {
                break;
            }
        }
    }

    private boolean isAlphabet(char c) {
        return Character.isLowerCase(c);
    }

    private void add(String str) {
        if (isEquationEmpty()) {
            equ = "";
        }
        equ = equ.replace(",", "");
        equ += str;
        if (enableNumberFormatter)
            equ = formatEquation(equ);
        equation.setText(equ);
    }

    private boolean isEquationEmpty() {
        String eq = equ;
        if (eq.equals(""))
            return true;
        return false;
    }

    private boolean ifPrevOperator() {
        if (equ.equals(""))
            return true;
        char c = equ.charAt(equ.length() - 1);
        return isOperator(c);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("equ", equ);
        outState.putBoolean("ifDeg", ifDegree);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equ = savedInstanceState.getString("equ");
        ifDegree = savedInstanceState.getBoolean("ifDeg");

        equation.setText(equ);
    }

    private boolean canPlaceDecimal() {
        String eq = equ;

        int j = eq.length() - 1;
        int count = 0;
        while (j >= 0 && !isOperator(eq.charAt(j))) {
            if (eq.charAt(j) == '.')
                count++;
            j--;
        }
        if (count == 0)
            return true;
        else
            return false;
    }

    private void animateClear(View viewRoot) {
        int cx = viewRoot.getRight();
        int cy = viewRoot.getBottom();
        int l = viewRoot.getHeight();
        int b = viewRoot.getWidth();
        int finalRadius = (int) Math.sqrt((l * l) + (b * b));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
            viewRoot.setVisibility(View.VISIBLE);
            anim.setDuration(300);
            anim.addListener(listener);
            anim.start();
        }
    }

    private Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            view.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        //check if number formatter is enabled
        enableNumberFormatter = preferences.getBooleanPreference(AppPreferences.APP_NUMBER_FORMATTER);
        //check if smart calculations is enabled
        enableSmartCalculation = preferences.getBooleanPreference(AppPreferences.APP_SMART_CALCULATIONS);

        if (Evaluate.balancedParenthesis(equ)) {
            result.setTextColor(getTextColor());
            result.setText(Evaluate.calculateResult(equ, enableNumberFormatter, MainActivity.this));
        } else {

            //trying to balance equation coz it's a smart calculator
            tempEqu = Evaluate.tryBalancingBrackets(equ);

            //if smart calculations is on and was able to balance the equation
            if (Evaluate.balancedParenthesis(tempEqu) && enableSmartCalculation) {
                result.setTextColor(getTextColor());
                result.setText(Evaluate.calculateResult(tempEqu, enableNumberFormatter, MainActivity.this));
            } else {
                result.setText("");
                Evaluate.errMsg = "Invalid Expression";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int resId = item.getItemId();
        Intent intent;

        switch (resId) {
            case R.id.settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.tutorial:
                startTutorial();
                break;

            case R.id.about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.history:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.history_icon:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPadViewPager == null || mPadViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first pad (or the pad is not paged),
            // allow the system to handle the Back button.
            finishAffinity();
        } else {
            // Otherwise, select the previous pad.
            mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
        }
    }

    private void startTutorial() {

        TapTargetSequence tapTargetSequence = new TapTargetSequence(this);

        tapTargetSequence.targets(
                TapTarget.forView(mainLayout.findViewById(R.id.del),
                        "Delete Button", "Simply LONG PRESS DELETE button to clear the " +
                                "calculator screen")
                        .outerCircleColor(R.color.colorBluePrimary)
                        .outerCircleAlpha(0.90f)
                        .targetCircleColor(R.color.colorWhite)
                        .titleTextSize(28)
                        .tintTarget(false)
                        .titleTextColor(R.color.colorWhite)
                        .descriptionTextColor(R.color.colorWhite)
                        .descriptionTextSize(18)
                        .cancelable(false),
                TapTarget.forToolbarMenuItem(toolbar, R.id.deg, "Angle Button",
                        "This is the angle button. Click here to change angle from " +
                                "DEGREES to RADIANS and vice versa.")
                        .outerCircleColor(R.color.colorBluePrimary)
                        .outerCircleAlpha(0.9f)
                        .targetCircleColor(R.color.colorWhite)
                        .titleTextSize(28)
                        .tintTarget(false)
                        .titleTextColor(R.color.colorWhite)
                        .descriptionTextColor(R.color.colorWhite)
                        .descriptionTextSize(18)
                        .cancelable(false),
                TapTarget.forToolbarOverflow(toolbar, "Options Menu", "This is options " +
                        "menu. This will help you to change app settings and preferences. Click " +
                        "here to open the menu.")
                        .outerCircleColor(R.color.colorBluePrimary)
                        .outerCircleAlpha(0.90f)
                        .targetCircleColor(R.color.colorWhite)
                        .titleTextSize(28)
                        .titleTextColor(R.color.colorWhite)
                        .descriptionTextColor(R.color.colorWhite)
                        .descriptionTextSize(18)
                        .cancelable(false)
        );
        tapTargetSequence.start();
    }

    private int getTextColor() {
        String theme = preferences.getStringPreference(AppPreferences.APP_THEME);

        if (theme.equals(Theme.DEFAULT) || theme.equals(Theme.MATERIAL_LIGHT)) {
            return getResources().getColor(R.color.colorBlack);
        }
        return getResources().getColor(R.color.colorWhite);
    }

    private void initialiseVariables() {
        //Initialisations
        mainLayout = findViewById(R.id.mainLayout);
        slidingLayout = findViewById(R.id.slidingLayout);
        mPadViewPager = findViewById(R.id.pad_pager);
        equation = findViewById(R.id.et_display1);
        result = findViewById(R.id.tv_display);
        b1 = mainLayout.findViewById(R.id.one);
        b2 = mainLayout.findViewById(R.id.two);
        b3 = mainLayout.findViewById(R.id.three);
        b4 = mainLayout.findViewById(R.id.four);
        b5 = mainLayout.findViewById(R.id.five);
        b6 = mainLayout.findViewById(R.id.six);
        b7 = mainLayout.findViewById(R.id.seven);
        b8 = mainLayout.findViewById(R.id.eight);
        b9 = mainLayout.findViewById(R.id.nine);
        b0 = mainLayout.findViewById(R.id.zero);
        badd = mainLayout.findViewById(R.id.add);
        bsub = mainLayout.findViewById(R.id.sub);
        bmul = mainLayout.findViewById(R.id.mul);
        bdiv = mainLayout.findViewById(R.id.div);
        bequal = mainLayout.findViewById(R.id.equal);
        bdel = mainLayout.findViewById(R.id.del);
        bdecimal = mainLayout.findViewById(R.id.decimal);
        open = mainLayout.findViewById(R.id.open);
        close = mainLayout.findViewById(R.id.close);
        percent = mainLayout.findViewById(R.id.percent);

        sin = slidingLayout.findViewById(R.id.sin);
        cos = slidingLayout.findViewById(R.id.cos);
        tan = slidingLayout.findViewById(R.id.tan);
        asin = slidingLayout.findViewById(R.id.asin);
        acos = slidingLayout.findViewById(R.id.acos);
        atan = slidingLayout.findViewById(R.id.atan);
        exp = slidingLayout.findViewById(R.id.exp);
        log = slidingLayout.findViewById(R.id.log);
        ln = slidingLayout.findViewById(R.id.ln);
        pow = slidingLayout.findViewById(R.id.pow);
        factorial = slidingLayout.findViewById(R.id.fact);
        sqrt = slidingLayout.findViewById(R.id.sqroot);
        cbrt = slidingLayout.findViewById(R.id.cuberoot);
        pi = slidingLayout.findViewById(R.id.pi);
        ms = slidingLayout.findViewById(R.id.ms);
        mr = slidingLayout.findViewById(R.id.mr);
        mPlus = slidingLayout.findViewById(R.id.mplus);
        mMinus = slidingLayout.findViewById(R.id.mminus);

        view = findViewById(R.id.view2);
        toolbar = findViewById(R.id.toolbar);

        //adding history
        history = new History(this);

        //adding onClickListeners
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        b0.setOnClickListener(this);
        badd.setOnClickListener(this);
        bsub.setOnClickListener(this);
        bmul.setOnClickListener(this);
        bdiv.setOnClickListener(this);
        bequal.setOnClickListener(this);
        bdel.setOnClickListener(this);
        bdecimal.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        percent.setOnClickListener(this);
        sin.setOnClickListener(this);
        cos.setOnClickListener(this);
        tan.setOnClickListener(this);
        asin.setOnClickListener(this);
        acos.setOnClickListener(this);
        atan.setOnClickListener(this);
        exp.setOnClickListener(this);
        log.setOnClickListener(this);
        ln.setOnClickListener(this);
        pow.setOnClickListener(this);
        factorial.setOnClickListener(this);
        sqrt.setOnClickListener(this);
        cbrt.setOnClickListener(this);
        pi.setOnClickListener(this);
        ms.setOnClickListener(this);
        mr.setOnClickListener(this);
        mPlus.setOnClickListener(this);
        mMinus.setOnClickListener(this);

    }

    private boolean isOperator(char c) {
        if (c == '+' ||
                c == '/' ||
                c == '*' ||
                c == '%' ||
                c == '!' ||
                c == '^' ||
                c == '\u221a' ||
                c == '\u221b' ||
                c == '÷' ||
                c == '\u00d7' ||
                c == '-')
            return true;
        return false;
    }

    //adds number formatter (,) to the number string
    private String formatString(String str) {
        int index = str.indexOf('.');
        if (index == -1)
            index = str.length();
        int temp = 0;
        for (int i = index - 1; i > 0; i--) {
            temp++;

            if (temp % 3 == 0) {
                temp = 0;
                if (i == 1 && str.charAt(0) == '-')
                    break;
                str = str.substring(0, i) + "," + str.substring(i);
            }
        }
        return str;
    }

    //adds number formatter (,) to the equation
    private String formatEquation(String equation) {
        Stack<String> stack = new Stack<>();
        char c;
        String temp = "";
        for (int i = 0; i < equation.length(); i++) {
            c = equation.charAt(i);
            if (isOperator(c)) {
                if (!temp.equals(""))
                    stack.push(temp);
                stack.push(c + "");
                temp = "";
            } else if (c == '(' || c == ')') {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                stack.push(c + "");
            } else {
                temp = temp + c;
            }
        }

        if (!temp.equals(""))
            stack.push(temp);

        Stack<String> abc = new Stack<>();
        while (!stack.empty()) {
            abc.push(stack.pop());
        }

        StringBuilder builder = new StringBuilder();
        while (!abc.empty()) {
            if (isNumber(abc.peek())) {
                builder.append(formatString(abc.pop()));
            } else {
                builder.append(abc.pop());
            }
        }

        return builder.toString();
    }

    public void clickDeg(MenuItem item) {
        String text = item.getTitle().toString();
        if (text.equals("DEG")) {
            item.setTitle("RAD");
            ifDegree = false;
        } else if (text.equals("RAD")) {
            item.setTitle("DEG");
            ifDegree = true;
        }
        preferences.setBooleanPreference(AppPreferences.APP_ANGLE, ifDegree);
        afterTextChanged(equation.getText());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (ifDegree) {
                menu.findItem(R.id.deg).setTitle("DEG");
            } else {
                menu.findItem(R.id.deg).setTitle("RAD");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private static boolean isNumber(String string) {
        return Pattern.matches("-?\\d+(\\.\\d+)?", string);
    }

    private boolean isNumber(char c) {
        switch (c) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
            case 'e':
            case '\u03c0':
                return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.setBooleanPreference(AppPreferences.APP_ANGLE, ifDegree);
        preferences.setStringPreference(AppPreferences.APP_EQUATION_STRING,
                equation.getText().toString().trim());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setting menu for DEG or RAD
        ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);
        onPrepareOptionsMenu(menu);

        //checking if called by history intent
        boolean historySet = preferences.getBooleanPreference(AppPreferences.APP_HISTORY_SET);
        if (historySet) {
            equ = preferences.getStringPreference(AppPreferences.APP_HISTORY_EQUATION);
            preferences.setBooleanPreference(AppPreferences.APP_HISTORY_SET, false);
        } else {
            equ = preferences.getStringPreference(AppPreferences.APP_EQUATION_STRING);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (enableNumberFormatter) {
            equ = equ.replaceAll(",", "");
            equ = formatEquation(equ);
        } else {
            equ = equ.replaceAll(",", "");
        }
        equation.setText(equ);
    }
}
