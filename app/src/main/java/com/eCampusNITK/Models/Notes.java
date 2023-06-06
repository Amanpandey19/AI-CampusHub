package com.eCampusNITK.Models;

public class Notes {
    String NotesName;
    String url;
    String description;
    String author;
    String postedDate;

    public Notes(String notesName, String url, String description, String author, String postedDate) {
        NotesName = notesName;
        this.url = url;
        this.description = description;
        this.author = author;
        this.postedDate = postedDate;
    }

    public Notes() {
    }

    public String getNotesName() {
        return NotesName;
    }

    public void setNotesName(String notesName) {
        NotesName = notesName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
}
