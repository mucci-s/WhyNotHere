package com.mobile.whynothere.models;

import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String author;
    private String comment;
    Integer photoProfile;



    public Comment(String author, String comment, Integer aphotoProfile) {
        this.author = author;
        this.comment = comment;
        this.photoProfile = aphotoProfile;
    }

    public Integer getPhotoProfile() { return photoProfile; }
    public String getAuthor() {
        return author;
    }
    public String getComment() {
        return comment;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


    public void setPhotoProfile(Integer photoProfile) { this.photoProfile = photoProfile; }


    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
