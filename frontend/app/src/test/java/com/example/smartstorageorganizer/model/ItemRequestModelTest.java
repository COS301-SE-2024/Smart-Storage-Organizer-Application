package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemRequestModelTest {

    private ItemRequestModel itemRequest;

    @Before
    public void setUp() {
        itemRequest = new ItemRequestModel(
                "req123", "Laptop", "A high-end gaming laptop", "Warehouse A",
                "Electronics", "Laptops", "#FF5733", "user@example.com",
                "org456", "2024-10-01", "TypeA", "Pending", "item789"
        );
    }

    @Test
    public void testGetRequestId() {
        assertEquals("req123", itemRequest.getRequestId());
    }

    @Test
    public void testSetRequestId() {
        itemRequest.setRequestId("req456");
        assertEquals("req456", itemRequest.getRequestId());
    }

    @Test
    public void testGetItemName() {
        assertEquals("Laptop", itemRequest.getItemName());
    }

    @Test
    public void testSetItemName() {
        itemRequest.setItemName("Desktop");
        assertEquals("Desktop", itemRequest.getItemName());
    }

    @Test
    public void testGetItemDescription() {
        assertEquals("A high-end gaming laptop", itemRequest.getItemDescription());
    }

    @Test
    public void testSetItemDescription() {
        itemRequest.setItemDescription("A powerful desktop computer");
        assertEquals("A powerful desktop computer", itemRequest.getItemDescription());
    }

    @Test
    public void testGetLocation() {
        assertEquals("Warehouse A", itemRequest.getLocation());
    }

    @Test
    public void testSetLocation() {
        itemRequest.setLocation("Warehouse B");
        assertEquals("Warehouse B", itemRequest.getLocation());
    }

    @Test
    public void testGetParentCategory() {
        assertEquals("Electronics", itemRequest.getParentCategory());
    }

    @Test
    public void testSetParentCategory() {
        itemRequest.setParentCategory("Furniture");
        assertEquals("Furniture", itemRequest.getParentCategory());
    }

    @Test
    public void testGetSubcategory() {
        assertEquals("Laptops", itemRequest.getSubcategory());
    }

    @Test
    public void testSetSubcategory() {
        itemRequest.setSubcategory("Desktops");
        assertEquals("Desktops", itemRequest.getSubcategory());
    }

    @Test
    public void testGetColorCode() {
        assertEquals("#FF5733", itemRequest.getColorCode());
    }

    @Test
    public void testSetColorCode() {
        itemRequest.setColorCode("#0000FF");
        assertEquals("#0000FF", itemRequest.getColorCode());
    }

    @Test
    public void testGetUserEmail() {
        assertEquals("user@example.com", itemRequest.getUserEmail());
    }

    @Test
    public void testSetUserEmail() {
        itemRequest.setUserEmail("newuser@example.com");
        assertEquals("newuser@example.com", itemRequest.getUserEmail());
    }

    @Test
    public void testGetOrganizationId() {
        assertEquals("org456", itemRequest.getOrganizationId());
    }

    @Test
    public void testSetOrganizationId() {
        itemRequest.setOrganizationId("org789");
        assertEquals("org789", itemRequest.getOrganizationId());
    }

    @Test
    public void testGetRequestDate() {
        assertEquals("2024-10-01", itemRequest.getRequestDate());
    }

    @Test
    public void testSetRequestDate() {
        itemRequest.setRequestDate("2024-11-01");
        assertEquals("2024-11-01", itemRequest.getRequestDate());
    }

    @Test
    public void testGetRequestType() {
        assertEquals("TypeA", itemRequest.getRequestType());
    }

    @Test
    public void testSetRequestType() {
        itemRequest.setRequestType("TypeB");
        assertEquals("TypeB", itemRequest.getRequestType());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Pending", itemRequest.getStatus());
    }

    @Test
    public void testSetStatus() {
        itemRequest.setStatus("Approved");
        assertEquals("Approved", itemRequest.getStatus());
    }

    @Test
    public void testGetItemId() {
        assertEquals("item789", itemRequest.getItemId());
    }

    @Test
    public void testSetItemId() {
        itemRequest.setItemId("item123");
        assertEquals("item123", itemRequest.getItemId());
    }
}
