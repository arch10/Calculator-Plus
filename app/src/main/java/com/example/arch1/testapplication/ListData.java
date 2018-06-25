package com.example.arch1.testapplication;

public class ListData {
    private String title, body;
    private int img;

    public ListData() {
        title = "";
        body = "";
        img = 0;
    }

    public ListData(String title, String body, int img) {
        this.title = title;
        this.body = body;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
