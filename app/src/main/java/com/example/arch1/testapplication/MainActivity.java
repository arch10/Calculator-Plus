package com.example.arch1.testapplication;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

        //initialising variables
        initialiseVariables();

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
            equ = equ.replace("x", "*");

            return getAnswer(equ);
            //return df.format(getResult(equ));
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        char c;

        switch (id) {
            case R.id.mul:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%') {
                        add("x");
                        break;
                    }
                    if (c == '(') {
                        break;
                    }
                    if (ifPrevOperator()) {
                        if (equ.length() == 1) {
                            break;
                        }
                        if (removeBackOperators()) {
                            add("%x");
                        } else {
                            add("x");
                        }
                    } else if (c == '.') {
                        break;
                    } else {
                        add("x");
                    }
                }
                break;

            case R.id.div:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%') {
                        add("÷");
                        break;
                    }
                    if (c == '(') {
                        break;
                    }
                    if (ifPrevOperator()) {
                        if (equ.length() == 1) {
                            break;
                        }
                        if (removeBackOperators()) {
                            add("%÷");
                        } else {
                            add("÷");
                        }
                    } else if (c == '.') {
                        break;
                    } else {
                        add("÷");
                    }
                }
                break;

            case R.id.equal:
                if (!isEquationEmpty()) {
                    String res = result.getText().toString().trim();
                    if (res.equals("") || res.equals(getString(R.string.invalid_expression))) {
                        result.setText(getString(R.string.invalid_expression));
                        result.setTextColor(getResources().getColor(R.color.colorRed));
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        result.startAnimation(shake);
                        break;
                    }
                    if (!res.equals("")) {
                        equ = "";
                        equation.setText(res);
                        result.setText("");
                    }
                }
                break;

            case R.id.del:
                if (!isEquationEmpty()) {
                    equ = equ.substring(0, equ.length() - 1);
                    equation.setText(equ);
                } else {
                    equation.setText("");
                }
                break;

            case R.id.decimal:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c + "") && canPlaceDecimal()) {
                        add(".");
                        break;
                    }
                    if (ifPrevOperator()) {
                        add("0.");
                        break;
                    }
                    if (c == '(') {
                        add("0.");
                        break;
                    }
                    if (c == ')') {
                        add("x0.");
                        break;
                    }
                } else if (isEquationEmpty()) {
                    add("0.");
                }
                break;

            case R.id.add:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%') {
                        add("+");
                        break;
                    }
                    if (c == '(') {
                        break;
                    }
                    if (c == ')') {
                        add("+");
                        break;
                    }
                    if (isOperator(c)) {
                        if (removeBackOperators()) {
                            add("%+");
                        } else {
                            add("+");
                        }
                        break;
                    }

                    if (c != '.' && !isOperator(c))
                        add("+");
                    else if (isOperator(c)) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("+");
                    }
                    break;
                }
                if ((!ifPrevOperator()) || equ.equals("")) {
                    add("+");
                } else if (ifPrevOperator()) {
                    if (!isEquationEmpty()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("+");
                    }
                }
                break;

            case R.id.sub:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c == '%') {
                        add("-");
                        break;
                    }
                    if (c == '(') {
                        break;
                    }
                    if (c == ')') {
                        add("-");
                        break;
                    }

                    if (isOperator(c)) {
                        if (equ.length() >= 2 && (isNumber(equ.charAt(equ.length() - 2) + ""))) {
                            if (c == '-') {
                                removeBackOperators();
                                add("+");
                                break;
                            }
                            add("-");
                            break;
                        }
                        if (removeBackOperators()) {
                            add("%-");
                        } else {
                            add("-");
                        }
                        break;
                    }

                    if (c != '.' && !isOperator(c))
                        add("-");
                    else if (isOperator(c)) {

                        if (c == '+' || c == '÷' || c == 'x')
                            add("-");
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
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x1");
                    break;
                }
                add("1");
                break;

            case R.id.two:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x2");
                    break;
                }
                add("2");
                break;

            case R.id.three:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x3");
                    break;
                }
                add("3");
                break;

            case R.id.four:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x4");
                    break;
                }
                add("4");
                break;

            case R.id.five:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x5");
                    break;
                }
                add("5");
                break;

            case R.id.six:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x6");
                    break;
                }
                add("6");
                break;

            case R.id.seven:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x7");
                    break;
                }
                add("7");
                break;

            case R.id.eight:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x8");
                    break;
                }
                add("8");
                break;

            case R.id.nine:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x9");
                    break;
                }
                add("9");
                break;

            case R.id.zero:
                if (!isEquationEmpty() && (equ.charAt(equ.length() - 1) == ')' || equ.charAt(equ.length() - 1) == '%')) {
                    add("x0");
                    break;
                }
                add("0");
                break;

            case R.id.percent:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c + "") || c == ')') {
                        add("%");
                        break;
                    }
                    if (isOperator(c)) {
                        if (equ.length() == 1) {
                            break;
                        }
                        removeBackOperators();
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
                    add("x(");
                    break;
                }

                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (isNumber(c + "") || c == '%' || c == ')') {
                        add("x(");
                        break;
                    }
                    if (c == '(' || isOperator(c)) {
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
                    if (isNumber(c + "") || c == ')' || c == '%') {
                        add(")");
                        break;
                    }
                    if (c == '.') {
                        add("0)");
                        break;
                    }
                    if (c == '(') {
                        equ = equ.substring(0, equ.length() - 1);
                        equation.setText(equ);
                        break;
                    }
                }
                break;

            default:
                break;
        }
    }

    private boolean removeBackOperators() {
        boolean value = false;
        if (!isEquationEmpty()) {
            char c = equ.charAt(equ.length() - 1);
            while (isOperator(c)) {
                if (c == '%')
                    value = true;
                equ = equ.substring(0, equ.length() - 1);
                if (equ.length() == 0)
                    break;
                c = equ.charAt(equ.length() - 1);
            }
        }
        equation.setText(equ);
        return value;
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

//        for (int i = 0; i < equation.length(); i++) {
//            c = equation.charAt(i);
//            if (c == '%') {
//                if (i != equation.length() - 1) {
//                    equation = equation.substring(0, i) + "/100" + equation.substring(i + 1, equation.length());
//                } else {
//                    equation = equation.substring(0, i) + "/100";
//                }
//            }
//        }

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

        if(df==null){
            //setting app precision
            precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
            setPrecision(precision);
            df = new DecimalFormat(precisionString);
        }
        //return df.format(getResult(lll));
        return df.format(getValue(lll));
    }

    private double getResult(String input) {

        char c = input.charAt(input.length() - 1);

        while (isOperator(c) && c != '%') {
            input = input.substring(0, input.length() - 1);
            if (!input.equals(""))
                c = input.charAt(input.length() - 1);
        }

        /* Create stacks for operators and operands */
        Stack<Integer> op = new Stack<Integer>();
        Stack<Double> val = new Stack<Double>();

        /* Create temporary stacks for operators and operands */
        Stack<Integer> optmp = new Stack<Integer>();
        Stack<Double> valtmp = new Stack<Double>();

        /* Accept expression */
        input = "0" + input;
        input = input.replaceAll("-", "+-");
        input = input.replaceAll("(\\*\\+)", "*");
        input = input.replaceAll("(\\/\\+)", "/");
        input = input.replaceAll("(\\+\\+)", "+");

        /* Store operands and operators in respective stacks */
        String temp = "";
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '-')
                temp = "-" + temp;
            else if (ch != '+' && ch != '*' && ch != '/')
                temp = temp + ch;
            else {
                val.push(Double.parseDouble(temp));
                op.push((int) ch);
                temp = "";
            }
        }

        val.push(Double.parseDouble(temp));
        char operators[] = {'/', '*', '+'};

        /* Evaluation of expression */
        for (int i = 0; i < 3; i++) {
            boolean it = false;
            while (!op.isEmpty()) {
                int optr = op.pop();
                double v1 = val.pop();
                double v2 = val.pop();
                if (optr == operators[i]) {
                    /* if operator matches evaluate and store in temporary stack */
                    if (i == 0) {
                        valtmp.push(v2 / v1);
                        it = true;
                        break;
                    } else if (i == 1) {
                        valtmp.push(v2 * v1);
                        it = true;
                        break;
                    } else if (i == 2) {
                        valtmp.push(v2 + v1);
                        it = true;
                        break;
                    }
                } else {
                    valtmp.push(v1);
                    val.push(v2);
                    optmp.push(optr);
                }
            }

            /* Push back all elements from temporary stacks to main stacks */
            while (!valtmp.isEmpty()) {
                if (!op.empty() && op.peek() == '/') {
                    Double val1 = valtmp.pop();
                    Double val2 = val.pop();
                    val.push(val1);
                    val.push(val2);
                } else {
                    val.push(valtmp.pop());
                }
            }

            while (!optmp.isEmpty())
                op.push(optmp.pop());

            /* Iterate again for same operator */
            if (it)
                i--;
        }
        return val.pop();
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

    private boolean ifAnEquation(String equ) {
        return Pattern.matches(EVALUATION_PATTERN, equ);
    }

    private boolean ifPrevOperator() {
        if (equ.equals(""))
            return true;
        char c = equ.charAt(equ.length() - 1);
        return isOperator(c);
    }

    private boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '÷' || c == 'x' || c == '%')
            return true;
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("equ", equ);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equ = savedInstanceState.getString("equ");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
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

    private boolean isNumber(String str) {
        return Pattern.matches("\\d+(\\.\\d+)?", str);
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

        for (int i = 0; i < tempEqu.length(); i++) {
            char c = tempEqu.charAt(i);
            if (c == '(')
                a++;
            if (c == ')')
                b++;
        }

        if (a != b) {
            int num = -1;
            if (a > b) {
                num = a - b;
                char c = tempEqu.charAt(tempEqu.length() - 1);
                if (isNumber(c + "") || c == ')' || c == '%') {
                    tempEqu = tempEqu + ")";
                    num--;
                } else if (c == '.') {
                    tempEqu = tempEqu + "0)";
                    num--;
                } else if (c == '(') {
                    return;
                }

                while (num > 0) {
                    tempEqu = tempEqu + ")";
                    num--;
                }
            }
            if (a < b) {
                num = b - a;
                while (num > 0) {
                    tempEqu = "(" + tempEqu;
                    num--;
                }
            }
        } else {
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }


//        tempEqu = equ;
//        int numOfOpen = 0;
//
//        for(int i=0;i<tempEqu.length();i++){
//            char c = tempEqu.charAt(i);
//            if(c == '('){
//                numOfOpen++;
//            }
//            if (c == ')'){
//                if(numOfOpen == 0){
//                    tempEqu = "("+tempEqu;
//                }
//                numOfOpen--;
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

    }
}
