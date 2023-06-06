package com.eCampusNITK.Models;

public class Links {
    String link;
    String author;
    String description;
    String timeOfPosting;

    public Links(String link, String author, String description, String timeOfPosting) {
        this.link = link;
        this.author = author;
        this.description = description;
        this.timeOfPosting = timeOfPosting;
    }

    public Links() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeOfPosting() {
        return timeOfPosting;
    }

    public void setTimeOfPosting(String timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }
}
