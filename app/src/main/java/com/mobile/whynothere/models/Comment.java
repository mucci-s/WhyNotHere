package com.mobile.whynothere.models;

import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String author;
    private String comment;
    GridView album;
    Integer photoProfile;
    List<Integer> images;



    public Comment(String author, String comment, Integer aphotoProfile, List<Integer> album) {
        this.author = author;
        this.comment = comment;
        this.images = album;
        this.photoProfile = aphotoProfile;
    }

    public Integer getPhotoProfile() { return photoProfile; }
    public List<Integer> getImages() { return images; }
    public String getAuthor() {
        return author;
    }
    public String getComment() {
        return comment;
    }
    public GridView getAlbum() {
        return album;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setAlbum(GridView album) {
        this.album = album;
    }

    public void setPhotoProfile(Integer photoProfile) { this.photoProfile = photoProfile; }
    public void setImages(ArrayList<Integer> images) { this.images = images; }

    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                ", album=" + album +
                '}';
    }
}
