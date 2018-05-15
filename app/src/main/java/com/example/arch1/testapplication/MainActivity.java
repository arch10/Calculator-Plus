package com.example.arch1.testapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Stack;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView result;
    private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, badd, bsub, bmul, bdiv, bequal, bdel, bdecimal;
    private EditText equation;
    private String equ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            equ = savedInstanceState.getString("equ");

        //Initialisations
        equation = findViewById(R.id.et_display1);
        result = findViewById(R.id.tv_display);
        b1 = findViewById(R.id.one);
        b2 = findViewById(R.id.two);
        b3 = findViewById(R.id.three);
        b4 = findViewById(R.id.four);
        b5 = findViewById(R.id.five);
        b6 = findViewById(R.id.six);
        b7 = findViewById(R.id.seven);
        b8 = findViewById(R.id.eight);
        b9 = findViewById(R.id.nine);
        b0 = findViewById(R.id.zero);
        badd = findViewById(R.id.add);
        bsub = findViewById(R.id.sub);
        bmul = findViewById(R.id.mul);
        bdiv = findViewById(R.id.div);
        bequal = findViewById(R.id.equal);
        bdel = findViewById(R.id.del);
        bdecimal = findViewById(R.id.decimal);

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

        //avoiding keyboard input
        equation.setShowSoftInputOnFocus(false);
        equation.setTextIsSelectable(false);
        equation.setLongClickable(false);

        bdel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                equ = "";
                equation.setText(equ);
                result.setText("");
                return true;
            }
        });


        equation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ifAnEquation(equ)) {
                    calculateResult();
                } else {
                    result.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void calculateResult() {
        equ = equ.replace("÷", "/");
        equ = equ.replace("x", "*");

        Double ans = getResult(equ);
        DecimalFormat df = new DecimalFormat("#.######");

        equ = equ.replace("/", "÷");
        equ = equ.replace("*", "x");

        result.setText(df.format(ans));
    }

    //empty screen mul and div
    //delcimal on decimal

    @Override
    public void onClick(View v) {
        int id = v.getId();
        char c;

        switch (id) {
            case R.id.mul:
                c = equ.charAt(equ.length() - 1);
                if (!isEquationEmpty() && c != '.') {
                    if (ifPrevOperator()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("x");
                        equation.setText(equ);
                    } else {
                        add("x");
                    }
                }
                break;

            case R.id.div:
                c = equ.charAt(equ.length() - 1);
                if (!isEquationEmpty() && c != '.') {
                    if (ifPrevOperator()) {
                        equ = equ.substring(0, equ.length() - 1);
                        add("÷");
                        equation.setText(equ);
                    } else {
                        add("÷");
                    }
                }
                break;

            case R.id.equal:
                if (!isEquationEmpty()) {
                    String res = result.getText().toString().trim();
                    if (!res.equals("")) {
                        equ = res;
                        equation.setText(equ);
                        result.setText("");
                    }
                }
                break;

            case R.id.del:
                if (!isEquationEmpty()) {
                    equ = equ.substring(0, equ.length() - 1);
                    equation.setText(equ);
                    if (ifAnEquation(equ)) {
                        calculateResult();
                    } else {
                        result.setText("");
                    }
                }
                break;

            case R.id.decimal:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
                    if (c != '.') {
                        if (ifPrevOperator()) {
                            add("0.");
                        } else {
                            add(".");
                        }
                    }
                } else if (isEquationEmpty()) {
                    add("0.");
                }
                break;

            case R.id.add:
                if (!isEquationEmpty()) {
                    c = equ.charAt(equ.length() - 1);
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
                if(!isEquationEmpty()){
                    c = equ.charAt(equ.length() - 1);
                    if(c!='.' && !isOperator(c))
                        add("-");
                    else if (isOperator(c)) {
                        equ = equ.substring(0, equ.length() - 1);
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
                add("1");
                break;

            case R.id.two:
                add("2");
                break;

            case R.id.three:
                add("3");
                break;

            case R.id.four:
                add("4");
                break;

            case R.id.five:
                add("5");
                break;

            case R.id.six:
                add("6");
                break;

            case R.id.seven:
                add("7");
                break;

            case R.id.eight:
                add("8");
                break;

            case R.id.nine:
                add("9");
                break;

            case R.id.zero:
                add("0");
                break;

            default:
                break;
        }
    }

    private void add(String str) {
        if (isEquationEmpty()) {
            equ = "";
        }
        equ += str;
        equation.setText(equ);
    }


    private boolean isEquationEmpty() {
        String eq = equation.getText().toString().trim();
        if (eq.equals(""))
            return true;
        if (eq == null)
            return true;
        return false;
    }

    private double getResult(String input) {

        char c = input.charAt(input.length() - 1);
        if (c == '+' || c == '-' || c == '/' || c == '*') {
            input = input.substring(0, input.length() - 1);
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

        /* Store operands and operators in respective stacks */

        String temp = "";

        for (int i = 0; i < input.length(); i++)

        {

            char ch = input.charAt(i);

            if (ch == '-')

                temp = "-" + temp;

            else if (ch != '+' && ch != '*' && ch != '/')

                temp = temp + ch;

            else

            {

                val.push(Double.parseDouble(temp));

                op.push((int) ch);

                temp = "";

            }

        }

        val.push(Double.parseDouble(temp));

        /* Create char array of operators as per precedence */

        /* -ve sign is already taken care of while storing */

        char operators[] = {'/', '*', '+'};

        /* Evaluation of expression */

        for (int i = 0; i < 3; i++)

        {

            boolean it = false;

            while (!op.isEmpty())

            {

                int optr = op.pop();

                double v1 = val.pop();

                double v2 = val.pop();

                if (optr == operators[i])

                {

                    /* if operator matches evaluate and store in temporary stack */

                    if (i == 0)

                    {

                        valtmp.push(v2 / v1);

                        it = true;

                        break;

                    } else if (i == 1)

                    {

                        valtmp.push(v2 * v1);

                        it = true;

                        break;

                    } else if (i == 2)

                    {

                        valtmp.push(v2 + v1);

                        it = true;

                        break;

                    }

                } else

                {

                    valtmp.push(v1);

                    val.push(v2);

                    optmp.push(optr);

                }

            }

            /* Push back all elements from temporary stacks to main stacks */

            while (!valtmp.isEmpty())

                val.push(valtmp.pop());

            while (!optmp.isEmpty())

                op.push(optmp.pop());

            /* Iterate again for same operator */

            if (it)

                i--;

        }

        return val.pop();
    }

    private boolean ifAnEquation(String equ) {
        //return Pattern.matches("[-+]?[0-9]+\\.[0-9]+[-+\\/ * \\u00f7 x][0-9 - + \\/ * \\u00f7 x \\.]+",equ);
        //return Pattern.matches("[-+]?[0-9]+.[0-9]+[-+\\/*÷x][0-9-+\\/*÷x.]+",equ);
        //boolean bool = Pattern.matches("[-+]?[0-9]+\\.[0-9]+[-+\\/*][0-9-+*\\/\\.]+",equ);
        boolean bool = Pattern.matches("[-+]?\\d+(\\.\\d+)?[-+\\/*÷x](\\d+(\\.\\d+)?[-+\\/*÷x]?(\\d+(\\.\\d+)?)?)+", equ);
        //Toast.makeText(this,bool+"'"+equ+"'",Toast.LENGTH_SHORT).show();
        return bool;
    }

    //[-+]?\d+(\.\d+)?[-+\/*÷](\d+(\.\d+)?[-+\/*]?(\d+(\.\d+)?)?)+


    private boolean ifPrevOperator() {
        if (equ.equals(""))
            return true;
        char c = equ.charAt(equ.length() - 1);
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '÷' || c == 'x')
            return true;
        return false;
    }

    private boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '÷' || c == 'x')
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
        if (ifAnEquation(equ)) {
            calculateResult();
        } else {
            result.setText("");
        }
    }
}
