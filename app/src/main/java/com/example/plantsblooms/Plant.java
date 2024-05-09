package com.example.plantsblooms;

import java.io.Serializable;

public class Plant implements Serializable {
    private String title;
    private String imageUrl;
    private String description;
    private String type;

    // Constructor
    public Plant(String title, String imageUrl, String description, String type) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.type = type;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Other getters and setters for imageUrl, description, and type

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
