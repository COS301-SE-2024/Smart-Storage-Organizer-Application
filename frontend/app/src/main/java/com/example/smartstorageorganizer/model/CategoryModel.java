package com.example.smartstorageorganizer.model;

public class CategoryModel {
    private String categoryName;
    private String categoryID;
    private String imageUrl;
    private String parentCategoryID;

    public CategoryModel() {

    }
    public CategoryModel(String categoryName, String categoryID) {
        this.categoryName = categoryName;
        this.categoryID = categoryID;
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
        return categoryID;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
