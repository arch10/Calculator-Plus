package com.example.arch1.testapplication;

import android.content.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Stack;
import java.util.regex.Pattern;

public class Evaluate {

    public static String errMsg = "Invalid Expression";
    private static AppPreferences preferences = null;
    private static Boolean ifDegree = true;

    //called from activity to get result
    public static String calculateResult(String equ, Boolean enableNumberFormatter, Context context) {

        preferences = AppPreferences.getInstance(context);
        ifDegree = preferences.getBooleanPreference(AppPreferences.APP_ANGLE);
        if (!equ.equals("")) {
            equ = equ.replace("÷", "/");
            equ = equ.replace("\u00d7", "*");
            equ = equ.replace(",","");

            String ans = new Evaluate().getAnswer(equ);
            if (ans.equals("-0"))
                ans = "0";
            if(enableNumberFormatter)
                return formatString(ans);
            return ans;
        }
        return "";
    }

    //checks if the equation is balanced or not
    public static boolean balancedParenthesis(String s) {
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

    //tries to balance the equations with smart balancing
    public static String tryBalancingBrackets(String equ) {
        String tempEqu = equ;
        int a = 0, b = 0;

        if (tempEqu.charAt(tempEqu.length() - 1) == '(') {
            while (tempEqu.charAt(tempEqu.length() - 1) == '(') {
                tempEqu = tempEqu.substring(0, tempEqu.length() - 1);
                if (tempEqu.length() == 0)
                    return tempEqu;
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
            if (c == ')' && openBracketCount > 0) {
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

        while (reqOpen > 0) {
            tempEqu = "(" + tempEqu;
            reqOpen--;
        }

        while (reqClose > 0) {
            tempEqu = tempEqu + ")";
            reqClose--;
        }

        return tempEqu;
    }

    //adds thousand separator
    private static String formatString(String str) {
        int index = str.indexOf('.');
        if(index == -1)
            index = str.length();
        int temp = 0;
        for(int i = index-1; i>0; i--){
            temp++;

            if(temp%3 == 0){
                temp = 0;
                if(i==1 && str.charAt(0) == '-')
                    break;
                str = str.substring(0,i)+","+str.substring(i);
            }
        }
        return str;
    }

    //checks if the string provided is a number
    private static boolean isNumber(String string) {
        return Pattern.matches("-?\\d+(\\.\\d+)?", string);
    }

    //checks if the string provided is a sqroot or cbroot
    private boolean isRoot(String string) {
        if (string.equals("\u221a") || string.equals("\u221b")) {
            return true;
        }
        return false;
    }

    //rounds the provided number to user preference digits
    private String roundMyAnswer(String ans) {

        String precision = preferences.getStringPreference(AppPreferences.APP_ANSWER_PRECISION);
        BigDecimal num =  new BigDecimal(ans);

        num = num.setScale(getPrecision(precision), RoundingMode.HALF_UP);
        num = num.stripTrailingZeros();

        if(num.compareTo(new BigDecimal("0")) == 0)
            return "0";
        return num.toPlainString();
    }

    //get user defined number precision
    private int getPrecision(String precision) {

        if (precision.equals("")) {
            preferences.setStringPreference(AppPreferences.APP_ANSWER_PRECISION, "six");
            return 6;
        } else {
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
    }

    //solves sqroots and cbroots
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

    //returns factorial of a number
    private static BigInteger factorial(int n) {
        BigInteger offset = new BigInteger("1");
        if(n < 0){
            offset = new BigInteger("-1");
            n = -n;
        }
        // Initialize result
        BigInteger f = new BigInteger("1"); // Or BigInteger.ONE

        // Multiply f with 2, 3, ...N
        for (int i = 2; i <= n; i++)
            f = f.multiply(BigInteger.valueOf(i));

        return f.multiply(offset);
    }

    //checks if the character is an operator
    //it doesnot include '-' sign as it is handled
    //in the methods as '+-' sign, where '-' is a
    //part of the number
    //eg: '500' - '400' = '500' + '-400'
    private boolean isTestOperator(char c) {
        if (c == '+' ||
                c == '/' ||
                c == '*' ||
                c == '%' ||
                c == '!' ||
                c == '^' ||
                c == '\u221a' ||
                c == '\u221b')
            return true;
        return false;
    }

    //checks if the char can be a last character in a valid equation
    private boolean canBeLastChar(char c) {
        if (isNumber(c))
            return true;
        if (c == ')' || c == '%' || c == '!')
            return true;
        return false;
    }

    //checks if the given char is a number or a constant
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

    //you provide the equation, it will give the result
    private String getAnswer(String equation) {

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
        equation = equation.replaceAll("\\.\\)",")");
        equation = equation.replaceAll("\\^\\)",")");


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
                String dd = null;
                try {
                    dd = getValue(abc);
                } catch (Exception e) {
                    e.printStackTrace();
                    dd = null;
                    errMsg = "Invalid Expression";
                }
                if (dd == null)
                    return "";
                stack.push(dd);
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

        String dd = null;
        try {
            dd = getValue(abc);
        } catch (Exception e) {
            e.printStackTrace();
            dd = null;
            errMsg = "Invalid Expression";
        }
        if (dd != null) {
            return dd;
        } else
            return "";
    }

    //solves sub-equations without brackets
    //this is the method where actual calculations happen
    private String getValue(Stack<String> token) throws Exception{
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
            return tt;
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
                return roundMyAnswer(tt);
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

                    if(stack.size() >=2 && (stack.peek().equals("+") || stack.peek().equals("-"))) {

                        String op = stack.pop();
                        Stack<String> tempStack = new Stack<>();
                        while (!stack.empty()){
                            tempStack.push(stack.pop());
                        }

                        String tempAns = getValue(tempStack);
                        if(tempAns == null) {
                            errMsg = "Invalid Expression";
                            return null;
                        }

                        Double num = Double.parseDouble(tempAns);
                        num1 = ((num1/100) * num);

                        if(op.equals("+")){
                            num+=num1;
                        } else if(op.equals("-")) {
                            num-=num1;
                        }

                        stack.push(num+"");
                        break;
                    }
                    num1 = num1 / 100;
                    stack.push(num1 + "");
                    break;
                case "!":
                    if (!Pattern.matches("-?\\d+(\\.0)?", stack.peek())) {
                        errMsg = "Domain error";
                        return null;
                    }
                    int a = Integer.parseInt(stack.pop());
                    if(a > 50){
                        errMsg = "Number too large";
                        return null;
                    }
                    stack.push(factorial(a).toString());
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
                return roundMyAnswer(tt);
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
                return roundMyAnswer(tt);
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
                BigDecimal num1 = new BigDecimal(val1);
                BigDecimal num2 = new BigDecimal(val2);

                if (num2.compareTo(new BigDecimal("0")) == 0) {
                    errMsg = "Cannot divide by 0";
                    return null;
                }

                num1 = num1.divide(num2,15,RoundingMode.HALF_UP);
                val1 = num1.toPlainString();
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
                return roundMyAnswer(tt);
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
                BigDecimal num1 = new BigDecimal(val1);
                BigDecimal num2 = new BigDecimal(val2);

                num1 = num1.multiply(num2);
                val1 = num1.toPlainString();
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
                return roundMyAnswer(tt);
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
                BigDecimal num1 = new BigDecimal(val1);
                BigDecimal num2 = new BigDecimal(val2);

                num1 = num1.add(num2);
                val1 = num1.toPlainString();
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
                return roundMyAnswer(tt);
            } catch (NumberFormatException e) {
                errMsg = "Invalid Expression";
                return null;
            }
        }

        errMsg = "Invalid Expression";
        return null;
    }

}
