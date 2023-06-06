package com.eCampusNITK.Models;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Posts {
    String   name;
    String   post_date;
    String   profile_pic;
    String   caption;
    String   postUrl;
    ArrayList<String>  usersLikedThisPost;
    String   postID;
    String   userPostID;
    ArrayList<Comments> comments;
    boolean isExpanded;

    public Posts() {
    }

    public Posts(String name, String post_date, String profile_pic, String caption,
                 String postUrl, ArrayList<String> likes, String postID,
                 ArrayList<Comments> comments, String userPostID) {
        this.name = name;
        this.post_date = post_date;
        this.profile_pic = profile_pic;
        this.caption = caption;
        this.postUrl = postUrl;
        this.usersLikedThisPost = likes;
        this.postID = postID;
        this.comments = comments;
        this.isExpanded = false;
        this.userPostID  = userPostID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public ArrayList<String> getLikes() {
        return usersLikedThisPost;
    }

    public void setLikes(ArrayList<String> likes) {
        this.usersLikedThisPost = likes;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getUserPostID() {
        return userPostID;
    }

    public void setUserPostID(String userPostID) {
        this.userPostID = userPostID;
    }
}
