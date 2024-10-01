package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitRequestModelTest {

    private UnitRequestModel unitRequest;

    @Before
    public void setUp() {
        unitRequest = new UnitRequestModel(
                "Storage Unit 1", "100", "No constraints", "2m", "3m", "4m",
                "500kg", "user@example.com", "org123", "Pending", "2024-10-01",
                "req123", "TypeA"
        );
    }

    @Test
    public void testGetUnitName() {
        assertEquals("Storage Unit 1", unitRequest.getUnitName());
    }

    @Test
    public void testSetUnitName() {
        unitRequest.setUnitName("Storage Unit 2");
        assertEquals("Storage Unit 2", unitRequest.getUnitName());
    }

    @Test
    public void testGetCapacity() {
        assertEquals("100", unitRequest.getCapacity());
    }

    @Test
    public void testSetCapacity() {
        unitRequest.setCapacity("200");
        assertEquals("200", unitRequest.getCapacity());
    }

    @Test
    public void testGetConstraints() {
        assertEquals("No constraints", unitRequest.getConstraints());
    }

    @Test
    public void testSetConstraints() {
        unitRequest.setConstraints("Some constraints");
        assertEquals("Some constraints", unitRequest.getConstraints());
    }

    @Test
    public void testGetWidth() {
        assertEquals("2m", unitRequest.getWidth());
    }

    @Test
    public void testSetWidth() {
        unitRequest.setWidth("3m");
        assertEquals("3m", unitRequest.getWidth());
    }

    @Test
    public void testGetHeight() {
        assertEquals("3m", unitRequest.getHeight());
    }

    @Test
    public void testSetHeight() {
        unitRequest.setHeight("4m");
        assertEquals("4m", unitRequest.getHeight());
    }

    @Test
    public void testGetDepth() {
        assertEquals("4m", unitRequest.getDepth());
    }

    @Test
    public void testSetDepth() {
        unitRequest.setDepth("5m");
        assertEquals("5m", unitRequest.getDepth());
    }

    @Test
    public void testGetMaxWeight() {
        assertEquals("500kg", unitRequest.getMaxWeight());
    }

    @Test
    public void testSetMaxWeight() {
        unitRequest.setMaxWeight("600kg");
        assertEquals("600kg", unitRequest.getMaxWeight());
    }

    @Test
    public void testGetUserEmail() {
        assertEquals("user@example.com", unitRequest.getUserEmail());
    }

    @Test
    public void testSetUserEmail() {
        unitRequest.setUserEmail("newuser@example.com");
        assertEquals("newuser@example.com", unitRequest.getUserEmail());
    }

    @Test
    public void testGetOrganizationId() {
        assertEquals("org123", unitRequest.getOrganizationId());
    }

    @Test
    public void testSetOrganizationId() {
        unitRequest.setOrganizationId("org456");
        assertEquals("org456", unitRequest.getOrganizationId());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Pending", unitRequest.getStatus());
    }

    @Test
    public void testSetStatus() {
        unitRequest.setStatus("Approved");
        assertEquals("Approved", unitRequest.getStatus());
    }

    @Test
    public void testGetRequestDate() {
        assertEquals("2024-10-01", unitRequest.getRequestDate());
    }

    @Test
    public void testSetRequestDate() {
        unitRequest.setRequestDate("2024-11-01");
        assertEquals("2024-11-01", unitRequest.getRequestDate());
    }

    @Test
    public void testGetRequestId() {
        assertEquals("req123", unitRequest.getRequestId());
    }

    @Test
    public void testSetRequestId() {
        unitRequest.setRequestId("req456");
        assertEquals("req456", unitRequest.getRequestId());
    }

    @Test
    public void testGetRequestType() {
        assertEquals("TypeA", unitRequest.getRequestType());
    }

    @Test
    public void testSetRequestType() {
        unitRequest.setRequestType("TypeB");
        assertEquals("TypeB", unitRequest.getRequestType());
    }
}
