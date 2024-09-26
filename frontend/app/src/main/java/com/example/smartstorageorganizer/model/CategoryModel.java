package com.example.smartstorageorganizer.model;

public class CategoryModel {
    private String categoryName;
    private String categoryID;
    private String imageUrl;
    private String parentCategoryId;

    public CategoryModel() {

    }

    public CategoryModel(String categoryName, String categoryID, String imageUrl) {
        this.categoryName = categoryName;
        this.categoryID = categoryID;
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
