package com.example.smartstorageorganizer.model;

public class OrganizationModel {
    String organizationName;
    String organizationId;
    String createdAt;

    public OrganizationModel() {

    }

    public OrganizationModel(String organizationName, String organizationId, String createdAt) {
        this.organizationName = organizationName;
        this.organizationId = organizationId;
        this.createdAt = createdAt;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
