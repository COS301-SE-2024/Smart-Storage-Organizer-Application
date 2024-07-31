package com.example.smartstorageorganizer.model;

public class SearchResult {
    private String title;
    private String description;

    public SearchResult(){

    }

    public SearchResult(String title, String description) {
        this.title = title;
        this.description = description;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
