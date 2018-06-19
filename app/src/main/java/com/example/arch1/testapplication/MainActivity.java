package com.example.arch1.testapplication;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private TextView result;
    private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, badd, bsub, bmul, bdiv, bequal, bdel, bdecimal;
    private Button sin, cos, tan, asin, acos, atan, exp, log, ln, pow, factorial, sqrt, cbrt, pi;
    private Button open, close, percent;
    private EditText equation;
    private String equ = "", tempEqu;
    private View view;
    private Animator anim;
    private View mainLayout, slidingLayout;
    private AppPreferences preferences;
    private android.support.v7.widget.Toolbar toolbar;
    private boolean firstLauch;
    private DecimalFormat df;
    private String precisionString, precision;
    private String errMsg = "Invalid Expression";
    private Menu menu;
    private boolean ifDegree;
    private History history;

    private static final String EVALUATION_PATTERN = "[-+]?\\d+(\\.\\d+)?%?[-+\\/*÷x%]-?((\\d+(\\.\\d+)?%?[-+\\/*÷x%]?-?(\\d+(\\.\\d+)?)?%?)+)?";

    //[-+]?\d+(\.\d+)?%?[-+\/*÷x%]-?((\d+(\.\d+)?%?[-+\/*÷x%]?-?(\d+(\.\d+)?)?%?)+)?

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set Activity Theme
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            equ = savedInstanceState.getString("equ");

        //set ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //setting menu for DEG or RAD
        ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);

        //initialising variables
        initialiseVariables();

        //adding history
        history = new History(this);

        //checking if first Launch
        firstLauch = preferences.getBooleanPreference(AppPreferences.APP_FIRST_LAUNCH);
        if (firstLauch) {
            startTutorial();
            preferences.setBooleanPreference(AppPreferences.APP_FIRST_LAUNCH, false);
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
        }

        //setting toolbar style manually
        setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));

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

        //checking if called by history intent
        Intent intent = getIntent();
        if(intent !=null){
            String historyEqu = intent.getStringExtra("equation");
            if(historyEqu != null){
                equ = historyEqu;
                equation.setText(equ);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //setting app precision
        precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
        setPrecision(precision);
        df = new DecimalFormat(precisionString);
    }

    private String calculateResult(String equ) {
        if (!equ.equals("")) {
            equ = equ.replace("÷", "/");
            equ = equ.replace("\u00d7", "*");

            //return getAnswer(equ);
            //return df.format(getResult(equ));
            String ans = getTestAnswer(equ);
            if (ans.equals("-0"))
                ans = "0";
            return ans;
        }
        return "";
    }

    private boolean isOneError(String string) {
        if (string.equals("Invalid Expression") || string.equals("Domain error") || string.equals("Cannot divide by 0"))
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        v.playSoundEffect(SoundEffectConstants.CLICK);
//        Vibrator vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        vb.vibrate(50);

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
                }
                break;

            case R.id.equal:
                if (!isEquationEmpty()) {
                    String res = result.getText().toString().trim();
                    if (res.equals("") || isOneError(res)) {
                        result.setText(errMsg);
                        result.setTextColor(getResources().getColor(R.color.colorRed));
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        result.startAnimation(shake);
                        break;
                    }
                    if (!res.equals("")) {
                        String historyEqu = equ;
                        String historyVal = res;
                        history.addToHistory(historyEqu,historyVal,System.currentTimeMillis());
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
                                equation.setText(equ);
                                break;
                            }
                        }
                    }
                    equ = equ.substring(0, equ.length() - 1);
                    equation.setText(equ);
                } else {
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
                } else if (isEquationEmpty()) {
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
                }
                break;

            case R.id.sub:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%' || c == ')' || c == '!') {
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
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d71");
                    break;
                }
                add("1");
                break;

            case R.id.two:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d72");
                    break;
                }
                add("2");
                break;

            case R.id.three:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d73");
                    break;
                }
                add("3");
                break;

            case R.id.four:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d74");
                    break;
                }
                add("4");
                break;

            case R.id.five:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d75");
                    break;
                }
                add("5");
                break;

            case R.id.six:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d76");
                    break;
                }
                add("6");
                break;

            case R.id.seven:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d77");
                    break;
                }
                add("7");
                break;

            case R.id.eight:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d78");
                    break;
                }
                add("8");
                break;

            case R.id.nine:
                if (!isEquationEmpty() && testNumber()) {
                    add("\u00d79");
                    break;
                }
                add("9");
                break;

            case R.id.zero:
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
                    if (isNumber(c + "") || c == ')' || c == '%') {
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
                    add("\u03c0");
                    break;
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
        equ += str;
        equation.setText(equ);
    }

    private boolean isEquationEmpty() {
        String eq = equ;//equation.getText().toString().trim();
        if (eq.equals(""))
            return true;
        if (eq == null)
            return true;
        return false;
    }

    private String getAnswer(String equation) {

        char c = equation.charAt(equation.length() - 1);

        while (isOperator(c) && c != '%') {
            equation = equation.substring(0, equation.length() - 1);
            if (!equation.equals(""))
                c = equation.charAt(equation.length() - 1);
            else
                return "";
        }

        Stack<String> stack = new Stack<>();
        String temp = "";
        for (int i = 0; i < equation.length(); i++) {
            c = equation.charAt(i);
            if (isOperator(c)) {
                if (!temp.equals(""))
                    stack.push(temp);
                stack.push(c + "");
                temp = "";
            } else if (c == '(') {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                stack.push("(");
            } else if (c == ')') {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                String abc = "";
                while (!stack.peek().equals("(")) {
                    abc = stack.pop() + abc;
                }
                stack.pop();
                //stack.push(df.format(getResult(abc)));
                stack.push(df.format(getValue(abc)));
            } else {
                temp = temp + c;
            }
        }

        if (!temp.equals(""))
            stack.push(temp);

        String lll = "";
        while (!stack.empty()) {
            lll = stack.pop() + lll;
        }

        if (df == null) {
            //setting app precision
            precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
            setPrecision(precision);
            df = new DecimalFormat(precisionString);
        }
        //return df.format(getResult(lll));
        return df.format(getValue(lll));
    }

    private double getValue(String equation) {

        char c = equation.charAt(equation.length() - 1);

        while (isOperator(c) && c != '%') {
            equation = equation.substring(0, equation.length() - 1);
            if (!equation.equals(""))
                c = equation.charAt(equation.length() - 1);
        }

        String temp = "";
        Stack<String> stack = new Stack<>();
        Stack<String> workingStack = new Stack<>();

        equation = "0" + equation;
        equation = equation.replaceAll("-", "+-");
        equation = equation.replaceAll("(\\*\\+)", "*");
        equation = equation.replaceAll("(\\/\\+)", "/");
        equation = equation.replaceAll("(\\+\\+)", "+");


        //tokenize
        temp = "";
        for (int i = 0; i < equation.length(); i++) {
            c = equation.charAt(i);

            if (isOp(c)) {
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

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }

        //unary operator
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.equals("%")) {
                String val1 = stack.pop();
                double num1 = Double.parseDouble(val1);
                num1 = num1 / 100;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }

        //division
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '/') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 / num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }

        //multiplication
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '*') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 * num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        }

        //addition
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '+') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 + num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if operation is over
        if (workingStack.size() == 1) {
            String ans = workingStack.pop();
            return Double.parseDouble(ans);
        } else
            return 0.0;
    }

    private boolean isOp(char c) {
        if (c == '+' || c == '/' || c == '*' || c == '%')
            return true;
        return false;
    }

    private boolean ifPrevOperator() {
        if (equ.equals(""))
            return true;
        char c = equ.charAt(equ.length() - 1);
        return isOperator(c);
    }

    private boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '÷' || c == '\u00d7' || c == '%' || c == '!' || c == '^')
            return true;
        return false;
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

        if (balancedParenthesis(equ)) {
            result.setTextColor(getTextColor());
            result.setText(calculateResult(equ));
        } else {
            //trying to balance equation
            // this is smart calculator
            tryBalancingBrackets();
            //if could balance the equation, calculate the result
            if (balancedParenthesis(tempEqu)) {
                //calculate result
                result.setTextColor(getTextColor());
                result.setText(calculateResult(tempEqu));
            } else {
                result.setText("");
            }
        }
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
        if (balancedParenthesis(equ)) {
            result.setTextColor(getTextColor());
            result.setText(calculateResult(equ));
            //result.setText(equ);
        } else {
            //trying to balance equation
            // this is smart calculator
            tryBalancingBrackets();
            //if could balance the equation, calculate the result
            if (balancedParenthesis(tempEqu)) {
                //calculate result
                result.setTextColor(getTextColor());
                result.setText(calculateResult(tempEqu));
                //result.setText(tempEqu);
            } else {
                result.setText("");
                errMsg = "Invalid Expression";
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
                intent = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.history_icon:
                intent = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void startTutorial() {
//        TapTargetView.showFor(this,
//                TapTarget.forToolbarOverflow(toolbar, "Options Menu", "This is options " +
//                        "menu. This will help you to change app settings and preferences. Click " +
//                        "here to open the menu.")
//                        .outerCircleColor(R.color.darkGray)
//                        .outerCircleAlpha(0.70f)
//                        .targetCircleColor(R.color.colorWhite)
//                        .titleTextSize(28)
//                        .titleTextColor(R.color.colorWhite)
//                        .descriptionTextColor(R.color.colorWhite)
//                        .descriptionTextSize(18)
//                        .cancelable(false)
//                , new TapTargetView.Listener() {
//                    @Override
//                    public void onTargetClick(TapTargetView view) {
//                        super.onTargetClick(view);
//                        openOptionsMenu();
//                    }
//                });

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

    private boolean balancedParenthesis(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    private void tryBalancingBrackets() {
        tempEqu = equ;
        int a = 0, b = 0;

        if(tempEqu.charAt(tempEqu.length()-1)=='('){
            while (tempEqu.charAt(tempEqu.length()-1)=='('){
                tempEqu = tempEqu.substring(0,tempEqu.length()-1);
                if(tempEqu.length()==0)
                    return;
            }
        }

        //test method
        int numOfPairs = 0;
        int openBracketCount = 0;

        for (int i = 0; i < tempEqu.length(); i++) {
            char c = tempEqu.charAt(i);
            if (c == '(') {
                openBracketCount++;
            }
            if (c == ')' && openBracketCount>0) {
                openBracketCount--;
                numOfPairs++;
            }
        }

        for (int i = 0; i < tempEqu.length(); i++) {
            char c = tempEqu.charAt(i);
            if (c == '(')
                a++;
            if (c == ')')
                b++;
        }

        int reqOpen = b - numOfPairs;
        int reqClose = a - numOfPairs;

        while (reqOpen>0){
            tempEqu = "(" + tempEqu;
            reqOpen--;
        }

        while (reqClose>0) {
            tempEqu = tempEqu + ")";
            reqClose--;
        }

//        if (a != b) {
//            int num = -1;
//            if (a > b) {
//                num = a - b;
//                char c = tempEqu.charAt(tempEqu.length() - 1);
//                if (isNumber(c + "") || c == ')' || c == '%') {
//                    tempEqu = tempEqu + ")";
//                    num--;
//                } else if (c == '.') {
//                    tempEqu = tempEqu + "0)";
//                    num--;
//                } else if (c == '(') {
//                    return;
//                }
//
//                while (num > 0) {
//                    tempEqu = tempEqu + ")";
//                    num--;
//                }
//            }
//            if (a < b) {
//                num = b - a;
//                while (num > 0) {
//                    tempEqu = "(" + tempEqu;
//                    num--;
//                }
//            }
//        } else if(a == b){
//            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//            int numOfPairs = 0;
//            int openBracketCount = 0;
//
//            for (int i = 0; i < tempEqu.length(); i++) {
//                char c = tempEqu.charAt(i);
//                if (c == '(') {
//                    openBracketCount++;
//                }
//                if (c == ')' && openBracketCount>0) {
//                    openBracketCount--;
//                    numOfPairs++;
//                }
//            }
//            int requiredPairs = a - numOfPairs;
//
//            while (requiredPairs>0){
//                tempEqu = "("+tempEqu+")";
//                requiredPairs--;
//            }
//        }

    }

    private int getTextColor() {
        String theme = preferences.getStringPreference(AppPreferences.APP_THEME);

        if (theme.equals("default") || theme.equals("")) {
            return getResources().getColor(R.color.colorBlack);
        }
        return getResources().getColor(R.color.colorWhite);
    }

    public void setPrecision(String precision) {
        if (precision.equals("")) {
            precisionString = "#.######";
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
        } else {
            switch (precision) {
                case "two":
                    precisionString = "#.##";
                    break;
                case "three":
                    precisionString = "#.###";
                    break;
                case "four":
                    precisionString = "#.####";
                    break;
                case "five":
                    precisionString = "#.#####";
                    break;
                case "six":
                    precisionString = "#.######";
                    break;
                case "seven":
                    precisionString = "#.#######";
                    break;
                case "eight":
                    precisionString = "#.########";
                    break;
                case "nine":
                    precisionString = "#.#########";
                    break;
                case "ten":
                    precisionString = "#.##########";
                    break;
                default:
                    precisionString = "#.######";
                    break;
            }
        }
    }

    private void setToolBarStyle(String themeName) {
        if (themeName.equals("green")) {
            toolbar.setBackground(getDrawable(R.drawable.green_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        } else if (themeName.equals("orange")) {
            toolbar.setBackground(getDrawable(R.drawable.orange_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        } else if (themeName.equals("blue")) {
            toolbar.setBackground(getDrawable(R.drawable.blue_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        } else if (themeName.equals("lgreen")) {
            toolbar.setBackground(getDrawable(R.drawable.lightgreen_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        } else if (themeName.equals("pink")) {
            toolbar.setBackground(getDrawable(R.drawable.pink_title));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void initialiseVariables() {
        //Initialisations
        mainLayout = findViewById(R.id.mainLayout);
        slidingLayout = findViewById(R.id.slidingLayout);
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

        view = findViewById(R.id.view2);

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

    }

    private static Integer fact(Integer n) {
        if (n >= 0) {
            if (n == 0)
                return 1;
            else
                return n * fact(n - 1);
        } else {
            return n * fact(-n - 1);
        }
    }

    private static boolean isTestOperator(char c) {
        if (c == '+' || c == '/' || c == '*' || c == '%' || c == '!' || c == '^' || c == '\u221a' || c == '\u221b')
            return true;
        return false;
    }

    private static boolean isNumber(String string) {
        return Pattern.matches("-?\\d+(\\.\\d+)?", string);
    }

    private boolean canBeLastChar(char c) {
        if (isNumber(c))
            return true;
        if (c == ')' || c == '%' || c == '!')
            return true;
        return false;
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

    //Test Methods
    private String getTestAnswer(String equation) {

        equation = equation.replaceAll("e", Math.E + "");
        equation = equation.replaceAll("\u03c0", "" + Math.PI);
        equation = equation.replaceAll("-", "+-");
        equation = equation.replaceAll("(\\*\\+)", "*");
        equation = equation.replaceAll("(\\/\\+)", "/");
        equation = equation.replaceAll("(\\+\\+)", "+");

        equation = equation.replaceAll("\\+\\)", ")");
        equation = equation.replaceAll("\\-\\)", ")");
        equation = equation.replaceAll("\\/\\)", ")");
        equation = equation.replaceAll("\\*\\)", ")");


        char c = equation.charAt(equation.length() - 1);

        while (!canBeLastChar(c)) {
            equation = equation.substring(0, equation.length() - 1);
            if (!equation.equals(""))
                c = equation.charAt(equation.length() - 1);
            else {
                errMsg = "Invalid Expression";
                return "";
            }
        }

        c = equation.charAt(0);
        if (c == '+' || c == '-')
            equation = "0" + equation;

        Stack<String> stack = new Stack<>();
        String temp = "";
        for (int i = 0; i < equation.length(); i++) {
            c = equation.charAt(i);
            if (isTestOperator(c)) {
                if (!temp.equals(""))
                    stack.push(temp);
                stack.push(c + "");
                temp = "";
            } else if (c == '(') {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                stack.push("(");
            } else if (c == ')') {
                if (!temp.equals("")) {
                    stack.push(temp);
                    temp = "";
                }
                Stack<String> abc = new Stack<>();
                while (!stack.peek().equals("(")) {
                    abc.push(stack.pop());
                }
                stack.pop();
                Double dd = getTestValue(abc);
                if (dd == null)
                    return "";
                stack.push(dd + "");
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

        Double dd = getTestValue(abc);
        if (dd != null) {
            if (df == null) {
                //setting app precision
                precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
                setPrecision(precision);
                df = new DecimalFormat(precisionString);
            }
            return df.format(dd);
        } else
            return "";
    }

    private Double getTestValue(Stack<String> token) {
        char c;
        String temp = "";
        Stack<String> stack = new Stack<>();
        Stack<String> workingStack = token;

        if (workingStack.contains("-")) {
            while (!workingStack.empty()) {
                temp = workingStack.pop();
                if (temp.equals("-")) {
                    temp = temp + workingStack.pop();

                }
                stack.push(temp);
            }

            while (!stack.empty()) {
                workingStack.push(stack.pop());
            }
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //solve for pre unary operators
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            Double num1;

            switch (temp) {
                case "sin":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (ifDegree)
                        num1 = Math.toRadians(num1);
                    stack.push(Math.sin(num1) + "");
                    break;
                case "cos":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (ifDegree)
                        num1 = Math.toRadians(num1);
                    stack.push(Math.cos(num1) + "");
                    break;
                case "tan":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (ifDegree) {
                        if (num1 % 90 == 0) {
                            errMsg = "Domain error";
                            return null;
                        }
                        num1 = Math.toRadians(num1);
                    } else {
                        if (num1 % (Math.PI / 2) == 0) {
                            errMsg = "Domain error";
                            return null;
                        }
                    }
                    stack.push(Math.tan(num1) + "");
                    break;
                case "asin":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (num1 > 1 || num1 < -1) {
                        errMsg = "Domain error";
                        return null;
                    }
                    if (ifDegree)
                        stack.push(Math.toDegrees(Math.asin(num1)) + "");
                    else
                        stack.push(Math.asin(num1) + "");
                    break;
                case "acos":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (num1 > 1 || num1 < -1) {
                        errMsg = "Domain error";
                        return null;
                    }
                    if (ifDegree)
                        stack.push(Math.toDegrees(Math.acos(num1)) + "");
                    else
                        stack.push(Math.acos(num1) + "");
                    break;
                case "atan":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (ifDegree)
                        stack.push(Math.toDegrees(Math.atan(num1)) + "");
                    else
                        stack.push(Math.atan(num1) + "");
                    break;
                case "log":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (num1 < 0) {
                        errMsg = "Domain error";
                        return null;
                    }
                    stack.push(Math.log10(num1) + "");
                    break;
                case "ln":
                    if (!isNumber(workingStack.peek())) {
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    if (num1 < 0) {
                        errMsg = "Domain error";
                        return null;
                    }
                    stack.push(Math.log(num1) + "");
                    break;
                case "\u221a":
                    if (!isNumber(workingStack.peek())) {
                        String ll = workingStack.peek();
                        if (ll.equals("\u221a") || ll.equals("\u221b")) {
                            Stack<String> gg = new Stack<>();
                            while (!workingStack.empty() && isRoot(ll)) {
                                gg.push(workingStack.pop());
                                if (!workingStack.empty())
                                    ll = workingStack.peek();
                            }
                            if (isNumber(workingStack.peek())) {
                                gg.push(workingStack.pop());
                            }
                            num1 = solveRoot(gg);
                            stack.push(Math.sqrt(num1) + "");
                            break;
                        }
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    stack.push(Math.sqrt(num1) + "");
                    break;
                case "\u221b":
                    if (!isNumber(workingStack.peek())) {
                        String ll = workingStack.peek();
                        if (ll.equals("\u221a") || ll.equals("\u221b")) {
                            Stack<String> gg = new Stack<>();
                            while (!workingStack.empty() && isRoot(ll)) {
                                gg.push(workingStack.pop());
                                if (!workingStack.empty())
                                    ll = workingStack.peek();
                            }
                            if (isNumber(workingStack.peek())) {
                                gg.push(workingStack.pop());
                            }
                            num1 = solveRoot(gg);
                            stack.push(Math.cbrt(num1) + "");
                            break;
                        }
                        errMsg = "Invalid Expression";
                        return null;
                    }
                    num1 = Double.parseDouble(workingStack.pop());
                    stack.push(Math.cbrt(num1) + "");
                    break;
                default:
                    stack.push(temp);
                    break;
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //solve for post unary operators
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();
            Double num1;

            switch (temp) {
                case "%":
                    num1 = Double.parseDouble(stack.pop());
                    num1 = num1 / 100;
                    stack.push(num1 + "");
                    break;
                case "!":
                    if (!Pattern.matches("-?\\d+(\\.0)?", stack.peek())) {
                        errMsg = "Domain error";
                        return null;
                    }
                    int a = Integer.parseInt(stack.pop());
                    stack.push(fact(a) + "");
                    break;
                default:
                    stack.push(temp);
                    break;
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //power  ^
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.equals("^")) {
                Double num1 = Double.parseDouble(stack.pop());
                Double num2 = Double.parseDouble(workingStack.pop());
                stack.push(Math.pow(num1, num2) + "");
            } else
                stack.push(temp);
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //division
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '/') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                if (num2 == 0) {
                    errMsg = "Cannot divide by 0";
                    return null;
                }

                num1 = num1 / num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //multiplication
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '*') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 * num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        //addition
        stack.clear();
        while (!workingStack.empty()) {
            temp = workingStack.pop();

            if (temp.length() == 1 && temp.charAt(0) == '+') {
                String val1 = stack.pop();
                String val2 = workingStack.pop();
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);

                num1 = num1 + num2;
                val1 = num1 + "";
                stack.push(val1);
            } else {
                stack.push(temp);
            }
        }

        while (!stack.empty()) {
            workingStack.push(stack.pop());
        }

        //check if solved
        if (workingStack.size() == 1) {
            String tt = workingStack.peek();
            try {
                return Double.parseDouble(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        errMsg = "Invalid Expression";
        return null;
    }

    private Double solveRoot(Stack<String> gg) {
        Double num = Double.parseDouble(gg.pop());
        while (!gg.empty()) {
            String kk = gg.pop();
            if (kk.equals("\u221a")) {
                num = Math.sqrt(num);
            }
            if (kk.equals("\u221b")) {
                num = Math.cbrt(num);
            }
        }
        return num;
    }

    private boolean isRoot(String string) {
        if (string.equals("\u221a") || string.equals("\u221b")) {
            return true;
        }
        return false;
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
        afterTextChanged(equation.getText());
        preferences.setBooleanPreference(AppPreferences.APP_ANGLE,ifDegree);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu != null){
            if(ifDegree){
                menu.findItem(R.id.deg).setTitle("DEG");
            } else {
                menu.findItem(R.id.deg).setTitle("RAD");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setAngle(){
        if(menu != null){
            if(ifDegree){
                menu.findItem(R.id.deg).setTitle("DEG");
            } else {
                menu.findItem(R.id.deg).setTitle("RAD");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.setBooleanPreference(AppPreferences.APP_ANGLE,ifDegree);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);
        setAngle();
    }
}
