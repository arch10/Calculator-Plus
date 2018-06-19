package com.example.arch1.testapplication;

public class Calculations {
    String equation, answer;
    String date;

    public Calculations(){
        equation = "";
        answer = "";
        date = "";
    }

    public Calculations(String equation, String answer, String date){
        this.equation = equation;
        this.answer = answer;
        this.date = date;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
