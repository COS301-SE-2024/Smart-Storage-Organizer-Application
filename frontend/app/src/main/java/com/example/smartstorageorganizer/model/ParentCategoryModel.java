package com.example.smartstorageorganizer.model;

public class ParentCategoryModel {
    private String categoryName;
    private String categoryID;

    public ParentCategoryModel() {

    }
    public ParentCategoryModel(String categoryName, String categoryID) {
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
}
