package com.eCampusNITK.Models;

import java.util.ArrayList;

public class MySubject {
    String subjectName;
    ArrayList<Links> subjectLinks;
    ArrayList<Notes> notes;
    Syllabus syllabus;

    public MySubject(String subjectName, ArrayList<Links> subjectLinks, ArrayList<Notes> notes, Syllabus syllabus) {
        this.subjectName = subjectName;
        this.subjectLinks = subjectLinks;
        this.notes = notes;
        this.syllabus = syllabus;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<Links> getSubjectLinks() {
        return subjectLinks;
    }

    public void setSubjectLinks(ArrayList<Links> subjectLinks) {
        this.subjectLinks = subjectLinks;
    }

    public ArrayList<Notes> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Notes> notes) {
        this.notes = notes;
    }

    public Syllabus getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(Syllabus syllabus) {
        this.syllabus = syllabus;
    }

    public MySubject() {
    }
}
