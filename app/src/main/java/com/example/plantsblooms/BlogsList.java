package com.example.plantsblooms;

import java.net.URI;
import java.util.Date;

public class BlogsList {
    private int id;
    private URI imageUri;
    private String userName; // Change from userEmail to userName
    private String blogText;
    private Date date;




    public BlogsList(int id, String blogText, String userName, Date date) { // Change from userEmail to userName
        this.id = id;
        this.blogText = blogText;
        this.userName = userName; // Change from userEmail to userName
        this.date = date;
        this.imageUri = imageUri;

    }


    public URI getImageUri() {
        return imageUri;
    }

    public void setImageUri(URI imageUri) {
        this.imageUri = imageUri;
    }


    public String getTitle() {
        int endIndex = Math.min(blogText.length(), 20);
        return blogText.substring(0, endIndex);
    }

    public String getDescription() {
        int startIndex = Math.min(blogText.length(), 20);
        return blogText.substring(startIndex);
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getBlogText() {
        return blogText;
    }

    public Date getDate() {
        return date;
    }
}
