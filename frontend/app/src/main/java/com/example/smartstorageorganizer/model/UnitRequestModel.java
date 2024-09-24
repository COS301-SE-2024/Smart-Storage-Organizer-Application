package com.example.smartstorageorganizer.model;

import com.google.firebase.Timestamp;

public class UnitRequestModel {
    String requestId;
    String unitName;
    String capacity;
    String constraints;
    String width;
    String height;
    String depth;
    String maxWeight;
    String userEmail;
    String organizationId;
    String status;
    String requestDate;
    String requestType;

    public UnitRequestModel(String unitName, String capacity, String constraints, String width, String height, String depth, String maxWeight, String userEmail, String organizationId, String status, String requestDate, String requestId, String requestType) {
        this.unitName = unitName;
        this.capacity = capacity;
        this.constraints = constraints;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.maxWeight = maxWeight;
        this.userEmail = userEmail;
        this.organizationId = organizationId;
        this.status = status;
        this.requestDate = requestDate;
        this.requestId = requestId;
        this.requestType = requestType;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(String maxWeight) {
        this.maxWeight = maxWeight;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
