package com.example.arch1.testapplication;

public class Calculations {
    String equation, answer;
    String date;

    public Calculations(String equation, String answer, String date) {
        this.equation = equation;
        this.answer = answer;
        this.date = date;
    }

    public String getEquation() {
        return equation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
