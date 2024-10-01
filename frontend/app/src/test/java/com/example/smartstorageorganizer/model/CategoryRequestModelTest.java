package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CategoryRequestModelTest {

    private CategoryRequestModel categoryRequest;

    @Before
    public void setUp() {
        categoryRequest = new CategoryRequestModel(
                "Electronics", 1, "2024-10-01", "Pending", "user@example.com",
                "http://example.com", "req123", "org456", "TypeA"
        );
    }

    @Test
    public void testGetCategoryName() {
        assertEquals("Electronics", categoryRequest.getCategoryName());
    }

    @Test
    public void testSetCategoryName() {
        categoryRequest.setCategoryName("Books");
        assertEquals("Books", categoryRequest.getCategoryName());
    }

    @Test
    public void testGetParentCategory() {
        assertEquals(1, categoryRequest.getParentCategory());
    }

    @Test
    public void testSetParentCategory() {
        categoryRequest.setParentCategory(2);
        assertEquals(2, categoryRequest.getParentCategory());
    }

    @Test
    public void testGetRequestDate() {
        assertEquals("2024-10-01", categoryRequest.getRequestDate());
    }

    @Test
    public void testSetRequestDate() {
        categoryRequest.setRequestDate("2024-11-01");
        assertEquals("2024-11-01", categoryRequest.getRequestDate());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Pending", categoryRequest.getStatus());
    }

    @Test
    public void testSetStatus() {
        categoryRequest.setStatus("Approved");
        assertEquals("Approved", categoryRequest.getStatus());
    }

    @Test
    public void testGetUserEmail() {
        assertEquals("user@example.com", categoryRequest.getUserEmail());
    }

    @Test
    public void testSetUserEmail() {
        categoryRequest.setUserEmail("newuser@example.com");
        assertEquals("newuser@example.com", categoryRequest.getUserEmail());
    }

    @Test
    public void testGetUrl() {
        assertEquals("http://example.com", categoryRequest.getUrl());
    }

    @Test
    public void testSetUrl() {
        categoryRequest.setUrl("http://newexample.com");
        assertEquals("http://newexample.com", categoryRequest.getUrl());
    }

    @Test
    public void testGetRequestId() {
        assertEquals("req123", categoryRequest.getRequestId());
    }

    @Test
    public void testSetRequestId() {
        categoryRequest.setRequestId("req456");
        assertEquals("req456", categoryRequest.getRequestId());
    }

    @Test
    public void testGetOrganizationId() {
        assertEquals("org456", categoryRequest.getOrganizationId());
    }

    @Test
    public void testSetOrganizationId() {
        categoryRequest.setOrganizationId("org789");
        assertEquals("org789", categoryRequest.getOrganizationId());
    }

    @Test
    public void testGetRequestType() {
        assertEquals("TypeA", categoryRequest.getRequestType());
    }

    @Test
    public void testSetRequestType() {
        categoryRequest.setRequestType("TypeB");
        assertEquals("TypeB", categoryRequest.getRequestType());
    }
}
