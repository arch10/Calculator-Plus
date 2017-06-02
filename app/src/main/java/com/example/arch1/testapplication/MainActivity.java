package com.example.arch1.testapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView text;
    private Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20;
    private float firstNum=0,secondNum=0;
    private int operator=-1;
    private boolean ifEqual=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization
        text=(TextView)findViewById(R.id.tv_display);
        b1=(Button)findViewById(R.id.one);
        b2=(Button)findViewById(R.id.two);
        b3=(Button)findViewById(R.id.three);
        b4=(Button)findViewById(R.id.four);
        b5=(Button)findViewById(R.id.five);
        b6=(Button)findViewById(R.id.six);
        b7=(Button)findViewById(R.id.seven);
        b8=(Button)findViewById(R.id.eight);
        b9=(Button)findViewById(R.id.nine);
        b0=(Button)findViewById(R.id.zero);
        b11=(Button)findViewById(R.id.multiply);
        b12=(Button)findViewById(R.id.add);
        b13=(Button)findViewById(R.id.subtract);
        b14=(Button)findViewById(R.id.divide);
        b15=(Button)findViewById(R.id.dot);
        b16=(Button)findViewById(R.id.posneg);
        b17=(Button)findViewById(R.id.equal);
        b18=(Button)findViewById(R.id.ce);
        b19=(Button)findViewById(R.id.c);
        b20=(Button)findViewById(R.id.delete);

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
        b11.setOnClickListener(this);
        b12.setOnClickListener(this);
        b13.setOnClickListener(this);
        b14.setOnClickListener(this);
        b15.setOnClickListener(this);
        b16.setOnClickListener(this);
        b17.setOnClickListener(this);
        b18.setOnClickListener(this);
        b19.setOnClickListener(this);
        b20.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.one:if(ifZero())
                text.setText("");
                text.append("1");
                break;
            case R.id.two:if(ifZero())
                text.setText("");
                text.append("2");
                break;
            case R.id.three:if(ifZero())
                text.setText("");
                text.append("3");
                break;
            case R.id.four:if(ifZero())
                text.setText("");
                text.append("4");
                break;
            case R.id.five:if(ifZero())
                text.setText("");
                text.append("5");
                break;
            case R.id.six:if(ifZero())
                text.setText("");
                text.append("6");
                break;
            case R.id.seven:if(ifZero())
                text.setText("");
                text.append("7");
                break;
            case R.id.eight:if(ifZero())
                text.setText("");
                text.append("8");
                break;
            case R.id.nine:if(ifZero())
                text.setText("");
                text.append("9");
                break;
            case R.id.zero:if(ifZero())
                text.setText("");
                text.append("0");
                break;
            case R.id.ce:text.setText("0");
                firstNum=0;
                secondNum=0;
                operator=-1;
                break;
            case R.id.c:text.setText("0");
                break;
            case R.id.equal:checkScreen();
                calculate();
                operator=-1;
                firstNum=0;
                break;
            case R.id.divide:operator(R.id.divide);
                break;
            case R.id.multiply:operator(R.id.multiply);
                break;
            case R.id.add:operator(R.id.add);
                break;
            case R.id.subtract:operator(R.id.subtract);
                break;
            case R.id.dot:if(ifZero())
                text.setText("0");
                text.append(".");
                break;
            case R.id.delete:if(ifZero())
                text.setText("0");
                else
            {
                String textArea=text.getText().toString().trim();
                textArea=textArea.substring(0,textArea.length()-1);
                if(textArea.length()==0)
                    textArea="0";
                text.setText(textArea);
            }
            break;
            case R.id.posneg:changeSign();
                break;
            default:break;
        }
    }

    private void changeSign() {
        String textArea=text.getText().toString().trim();
        if(textArea.startsWith("-")){
            textArea=textArea.substring(1,textArea.length());
        }
        else {
            textArea="-"+textArea;
        }
        text.setText(textArea);
    }

    private void noNeed(){
        //This is a useless function
        //This is just for checking GitHub
    }

    private void checkScreen() {
        if(text.getText().toString().equals("Error")||text.getText().toString().equals("Divide Error")){
            text.setText("0");
        }
    }

    private void operator(int id) {
        operator=id;
        String textArea=text.getText().toString().trim();
        if(!textArea.equals("0"))
        firstNum=Float.parseFloat(textArea);
        text.setText("0");
    }

    private void calculate() {
        String textArea=text.getText().toString().trim();
        BigDecimal result;
        secondNum=Float.parseFloat(textArea);
        float a = 0;
        switch (operator){
            case R.id.divide:if(secondNum==0){
                text.setText("Divide Error");
            }
            else {
                a=firstNum/secondNum;
                result=round(a,2);
                text.setText(result+"");
            }
                break;
            case R.id.multiply:
                a=firstNum*secondNum;
                result=round(a,2);
                text.setText(result+"");
                break;
            case R.id.add:
                a=firstNum+secondNum;
                result=round(a,2);
                text.setText(result+"");
                break;
            case R.id.subtract:
                a=firstNum-secondNum;
                result=round(a,2);
                text.setText(result+"");
                break;
            default:text.setText("Error");
                break;
        }
        ifEqual=true;
        firstNum=a;
    }

    boolean ifZero(){
        if(text.getText().toString().trim().equals("0")||
                text.getText().toString().trim().equals("Error")||
                text.getText().toString().trim().equals("Divide Error")||ifEqual){
            ifEqual=false;
        return true;}
        return false;
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
