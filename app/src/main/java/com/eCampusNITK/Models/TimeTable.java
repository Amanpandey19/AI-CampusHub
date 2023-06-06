package com.eCampusNITK.Models;

public class TimeTable {
    String timeTableUrl;
    String author;
    String postingTime;

    public TimeTable() {
    }

    public TimeTable(String timeTableUrl, String author, String postingTime) {
        this.timeTableUrl = timeTableUrl;
        this.author = author;
        this.postingTime = postingTime;
    }

    public String getTimeTableUrl() {
        return timeTableUrl;
    }

    public void setTimeTableUrl(String timeTableUrl) {
        this.timeTableUrl = timeTableUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(String postingTime) {
        this.postingTime = postingTime;
    }
}
