package com.kernelapps.onlinejobz.models;

import java.io.Serializable;
import java.util.Date;

public class JobItem implements Serializable {
    private String id;
    private String image;
    private String title;
    private String description;
    private Boolean isFavorite;
    private Date postedDate;
    private String categoryId;

    // Required for Gson
    public JobItem() {
    }

    public JobItem(String id, String image, String title, String description, Boolean isFavorite, Date postedDate, String categoryId) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.isFavorite = isFavorite;
        this.postedDate = postedDate;
        this.categoryId = categoryId;
    }

    // Add getters and setters
    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}
