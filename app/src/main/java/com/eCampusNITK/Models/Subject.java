package com.eCampusNITK.Models;

public class Subject {
    String subject_name;
    int total_classes;
    int attended_classes;
    String status;
    int percentage;

    public Subject(String subject_name, int total_classes, int attended_classes, String status, int percentage) {
        this.subject_name = subject_name;
        this.total_classes = total_classes;
        this.attended_classes = attended_classes;
        this.status = status;
        this.percentage = percentage;
    }

    public Subject() {
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public int getTotal_classes() {
        return total_classes;
    }

    public void setTotal_classes(int total_classes) {
        this.total_classes = total_classes;
    }

    public int getAttended_classes() {
        return attended_classes;
    }

    public void setAttended_classes(int attended_classes) {
        this.attended_classes = attended_classes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
