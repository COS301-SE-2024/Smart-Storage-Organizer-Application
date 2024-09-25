package com.example.smartstorageorganizer.model;

public class ModifyItemRequestModel {
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
    String itemId;
    String newItem = "";
    String oldItem = "";
    String newDescription = "";
    String oldDescription = "";
    String newLocation = "";
    String oldLocation = "";
    String newParentCategory = "";
    String oldParentCategory = "";
    String newSubcategory = "";
    String oldSubcategory = "";
    String newQuantity = "";
    String oldQuantity = "";

    public ModifyItemRequestModel(String requestId, String itemName, String itemDescription, String location, String parentCategory, String subcategory, String colorCode, String userEmail, String organizationId, String requestDate, String requestType, String status, String itemId) {
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
        this.itemId = itemId;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getNewItem() {
        return newItem;
    }

    public void setNewItem(String newItem) {
        this.newItem = newItem;
    }

    public String getOldItem() {
        return oldItem;
    }

    public void setOldItem(String oldItem) {
        this.oldItem = oldItem;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    public String getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(String oldLocation) {
        this.oldLocation = oldLocation;
    }

    public String getNewParentCategory() {
        return newParentCategory;
    }

    public void setNewParentCategory(String newParentCategory) {
        this.newParentCategory = newParentCategory;
    }

    public String getOldSubcategory() {
        return oldSubcategory;
    }

    public void setOldSubcategory(String oldSubcategory) {
        this.oldSubcategory = oldSubcategory;
    }

    public String getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(String newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(String oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public String getOldParentCategory() {
        return oldParentCategory;
    }

    public void setOldParentCategory(String oldParentCategory) {
        this.oldParentCategory = oldParentCategory;
    }

    public String getNewSubcategory() {
        return newSubcategory;
    }

    public void setNewSubcategory(String newSubcategory) {
        this.newSubcategory = newSubcategory;
    }
}
