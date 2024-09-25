package com.example.smartstorageorganizer.model;

public class ItemRequestModel {
    String requestId;
    String itemName;
    String itemDescription;
    String location;
    String parentCategory;
    String subcategory;
    String colorCode;
    String userEmail;
    String organizationId;
    String requestDate;
    String requestType;
    String status;

    public ItemRequestModel(String requestId, String itemName, String itemDescription, String location, String parentCategory, String subcategory, String colorCode, String userEmail, String organizationId, String requestDate, String requestType, String status) {
        this.requestId = requestId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = location;
        this.parentCategory = parentCategory;
        this.subcategory = subcategory;
        this.colorCode = colorCode;
        this.userEmail = userEmail;
        this.organizationId = organizationId;
        this.requestDate = requestDate;
        this.requestType = requestType;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
