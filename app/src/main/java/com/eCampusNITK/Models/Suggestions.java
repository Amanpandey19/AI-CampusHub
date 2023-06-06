package com.eCampusNITK.Models;

import android.graphics.drawable.Drawable;

public class Suggestions {
    String user_img;
    String   user_name;
    String   user_course;

    String   user_ID;

    public Suggestions() {
    }

    public Suggestions(String user_img, String user_name, String user_course, String user_ID) {
        this.user_img    = user_img;
        this.user_name   = user_name;
        this.user_course = user_course;
        this.user_ID     = user_ID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_course() {
        return user_course;
    }

    public void setUser_course(String user_course) {
        this.user_course = user_course;
    }

}
