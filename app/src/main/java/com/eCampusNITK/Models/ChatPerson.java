package com.eCampusNITK.Models;

import android.graphics.drawable.Drawable;

public class ChatPerson {
    String   person_image;
    String   person_name;
    int      last_text_status;
    String   last_text;
    String   last_text_time;
    String   notSeenMessages;
    String   lastTextPersonId;

    int      lastTextType;
    String   person_userId;

    public ChatPerson() {
    }

    public ChatPerson(String person_image, String person_name, int last_text_status, String last_text,
                      String last_text_time, String notSeenMessages, String lastTextPersonId,
                      int lastTextType, String person_userId) {
        this.person_image = person_image;
        this.person_name = person_name;
        this.last_text_status = last_text_status;
        this.last_text = last_text;
        this.last_text_time = last_text_time;
        this.notSeenMessages = notSeenMessages;
        this.lastTextPersonId = lastTextPersonId;
        this.lastTextType = lastTextType;
        this.person_userId = person_userId;
    }

    public String getPerson_userId() {
        return person_userId;
    }

    public void setPerson_userId(String person_userId) {
        this.person_userId = person_userId;
    }

    public int getLastTextType() {
        return lastTextType;
    }

    public void setLastTextType(int lastTextType) {
        this.lastTextType = lastTextType;
    }

    public String getLastTextPersonId() {
        return lastTextPersonId;
    }

    public void setLastTextPersonId(String lastTextPersonId) {
        this.lastTextPersonId = lastTextPersonId;
    }

    public String getPerson_image() {
        return person_image;
    }

    public void setPerson_image(String person_image) {
        this.person_image = person_image;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public int getLast_text_status() {
        return last_text_status;
    }

    public void setLast_text_status(int last_text_status) {
        this.last_text_status = last_text_status;
    }

    public String getLast_text() {
        return last_text;
    }

    public void setLast_text(String last_text) {
        this.last_text = last_text;
    }

    public String getLast_text_time() {
        return last_text_time;
    }

    public void setLast_text_time(String last_text_time) {
        this.last_text_time = last_text_time;
    }

    public String getNotSeenMessages() {
        return notSeenMessages;
    }

    public void setNotSeenMessages(String notSeenMessages) {
        this.notSeenMessages = notSeenMessages;
    }
}
