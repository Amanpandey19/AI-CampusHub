package com.eCampusNITK.Models;

public class Syllabus {
    private String syllabusUrl;
    private String postedBy;
    private String postedDate;

    public Syllabus() {
    }

    public String getSyllabusUrl() {
        return syllabusUrl;
    }

    public void setSyllabusUrl(String syllabusUrl) {
        this.syllabusUrl = syllabusUrl;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public Syllabus(String syllabusUrl, String postedBy, String postedDate) {
        this.syllabusUrl = syllabusUrl;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
    }
}
