package com.example.smartstorageorganizer.model;

public class SubcategoryReportModel {
    String subcategory;
    int totalNumberOfItems;

    public SubcategoryReportModel() {

    }
    public SubcategoryReportModel(String subcategory, int totalNumberOfItems) {
        this.subcategory = subcategory;
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public int getTotalNumberOfItems() {
        return totalNumberOfItems;
    }

    public void setTotalNumberOfItems(int totalNumberOfItems) {
        this.totalNumberOfItems = totalNumberOfItems;
    }
}
