package com.example.admin;

public class CustomerBanner {
    private String bannerId;
    private String imageUrl;
    private String linkUrl;
    private String title;
    private String description;
    private boolean active;
    private int displayOrder;

    // Required empty constructor for Firebase
    public CustomerBanner() {
    }

    public CustomerBanner(String bannerId, String imageUrl, String linkUrl, String title,
                          String description, boolean active, int displayOrder) {
        this.bannerId = bannerId;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.title = title;
        this.description = description;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}