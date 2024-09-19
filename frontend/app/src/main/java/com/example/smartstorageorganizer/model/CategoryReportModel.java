package com.example.smartstorageorganizer.model;

import java.util.List;

public class CategoryReportModel {
    String parentCategory;
    List<SubcategoryReportModel> subCategories;
    double totalNumberOfItems;
    public CategoryReportModel() {

    }
    public CategoryReportModel(String parentCategory, List<SubcategoryReportModel> subCategories, int totalNumberOfItems) {
        this.parentCategory = parentCategory;
        this.subCategories = subCategories;
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<SubcategoryReportModel> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubcategoryReportModel> subCategories) {
        this.subCategories = subCategories;
    }

    public double getTotalNumberOfItems() {
        return totalNumberOfItems;
    }

    public double getPercentage(double totalItems) {
        return (totalNumberOfItems / totalItems) * 100;
    }

    public void setTotalNumberOfItems(double totalNumberOfItems) {
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public double getTotalQuantity(){
        double total = 0;
        for(SubcategoryReportModel subcategory: subCategories){
            total += subcategory.getTotalQuantity();
        }
        return total;
    }
}
