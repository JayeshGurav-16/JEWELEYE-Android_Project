package com.example.admin;

public class CustomerCategory {
    private String categoryId;
    private String name;
    private String imageUrl;
    private String description;
    private int displayOrder;
    private boolean active;

    // Required empty constructor for Firebase
    public CustomerCategory() {
    }

    public CustomerCategory(String categoryId, String name, String imageUrl,
                            String description, int displayOrder, boolean active) {
        this.categoryId = categoryId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}