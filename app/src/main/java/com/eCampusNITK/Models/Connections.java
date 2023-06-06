package com.eCampusNITK.Models;

import android.graphics.drawable.Drawable;

public class Connections {
    String   user_img;
    String   user_name;
    String   user_course;
    String   connection_id;

    public Connections() {
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

    public Connections(String user_img, String user_name, String user_course, String connection_id) {
        this.user_img = user_img;
        this.user_name = user_name;
        this.user_course = user_course;
        this.connection_id = connection_id;
    }

    public String getConnection_id() {
        return connection_id;
    }

    public void setConnection_id(String connection_id) {
        this.connection_id = connection_id;
    }


}
