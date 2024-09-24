package com.example.smartstorageorganizer.model;

public class CategoryRequestModel {
    String categoryName;
    int parentCategory;
    String requestDate;
    String status;
    String userEmail;
    String url;
    String requestId;
    String organizationId;
    String requestType;

    public CategoryRequestModel(String categoryName, int parentCategory, String requestDate, String status, String userEmail, String url, String requestId, String organizationId, String requestType) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.requestDate = requestDate;
        this.status = status;
        this.userEmail = userEmail;
        this.url = url;
        this.requestId = requestId;
        this.organizationId = organizationId;
        this.requestType = requestType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(int parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
