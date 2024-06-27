package com.example.smartstorageorganizer.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryList {
    private List<CategoryModel> categoryModelList;

    public CategoryList(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    public List<CategoryModel> getCategoryModelList() {
        return categoryModelList;
    }

    public void setCategoryModelList(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    public List<String> getCategoriesNames() {
        List<String> categoriesNames = new ArrayList<>();

        for(CategoryModel category : categoryModelList){
            categoriesNames.add(category.getCategoryName());
        }

        return categoriesNames;
    }

    public List<String> getCategoriesId() {
        List<String> categoriesId = new ArrayList<>();

        for(CategoryModel category : categoryModelList){
            categoriesId.add(category.getCategoryID());
        }

        return categoriesId;
    }

    public CategoryModel findCategoryByName(String categoryName) {
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category with the given name is found
    }
}
