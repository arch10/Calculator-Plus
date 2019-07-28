package com.example.arch1.testapplication;

class ListData {
    private String title, body;
    private int img;

    ListData() {
        title = "";
        body = "";
        img = 0;
    }

    ListData(String title, String body, int img) {
        this.title = title;
        this.body = body;
        this.img = img;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getBody() {
        return body;
    }

    void setBody(String body) {
        this.body = body;
    }

    int getImg() {
        return img;
    }

    void setImg(int img) {
        this.img = img;
    }
}
