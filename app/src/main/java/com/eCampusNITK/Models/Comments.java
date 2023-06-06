package com.eCampusNITK.Models;

public class Comments {
    String personName;
    String personComment;
    String day;

    public Comments() {
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonComment() {
        return personComment;
    }

    public void setPersonComment(String personComment) {
        this.personComment = personComment;
    }

    public Comments(String personName, String personComment, String day) {
        this.personName = personName;
        this.personComment = personComment;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
