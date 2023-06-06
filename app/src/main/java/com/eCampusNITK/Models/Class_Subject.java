package com.eCampusNITK.Models;

public class Class_Subject {
    String subject_name;
    Boolean isSelected;

    public Class_Subject(String subject_name, Boolean isSelected) {
        this.subject_name = subject_name;
        this.isSelected = isSelected;
    }

    public Class_Subject() {
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
