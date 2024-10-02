package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemModelTest {

    private ItemModel item;

    @Before
    public void setUp() {
        item = new ItemModel(
                "1", "Laptop", "A high-end gaming laptop", "#FF5733", "123456789012",
                "QR123", "5", "Warehouse A", "user@example.com",
                "http://example.com/image.jpg", "2024-10-01"
        );
    }

    @Test
    public void testGetItemId() {
        assertEquals("1", item.getItemId());
    }

    @Test
    public void testSetItemId() {
        item.setItemId("2");
        assertEquals("2", item.getItemId());
    }

    @Test
    public void testGetItemName() {
        assertEquals("Laptop", item.getItemName());
    }

    @Test
    public void testSetItemName() {
        item.setItemName("Desktop");
        assertEquals("Desktop", item.getItemName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("A high-end gaming laptop", item.getDescription());
    }

    @Test
    public void testSetDescription() {
        item.setDescription("A powerful desktop computer");
        assertEquals("A powerful desktop computer", item.getDescription());
    }

    @Test
    public void testGetColourCoding() {
        assertEquals("#FF5733", item.getColourCoding());
    }

    @Test
    public void testSetColourCoding() {
        item.setColourCoding("#0000FF");
        assertEquals("#0000FF", item.getColourCoding());
    }

    @Test
    public void testGetBarcode() {
        assertEquals("123456789012", item.getBarcode());
    }

    @Test
    public void testSetBarcode() {
        item.setBarcode("987654321098");
        assertEquals("987654321098", item.getBarcode());
    }

    @Test
    public void testGetQrcode() {
        assertEquals("QR123", item.getQrcode());
    }

    @Test
    public void testSetQrcode() {
        item.setQrcode("QR456");
        assertEquals("QR456", item.getQrcode());
    }

    @Test
    public void testGetQuantity() {
        assertEquals("5", item.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        item.setQuantity("10");
        assertEquals("10", item.getQuantity());
    }

    @Test
    public void testGetLocation() {
        assertEquals("Warehouse A", item.getLocation());
    }

    @Test
    public void testSetLocation() {
        item.setLocation("Warehouse B");
        assertEquals("Warehouse B", item.getLocation());
    }

    @Test
    public void testGetEmail() {
        assertEquals("user@example.com", item.getEmail());
    }

    @Test
    public void testSetEmail() {
        item.setEmail("newuser@example.com");
        assertEquals("newuser@example.com", item.getEmail());
    }

    @Test
    public void testGetItemImage() {
        assertEquals("http://example.com/image.jpg", item.getItemImage());
    }

    @Test
    public void testSetItemImage() {
        item.setItemImage("http://example.com/newimage.jpg");
        assertEquals("http://example.com/newimage.jpg", item.getItemImage());
    }

    @Test
    public void testGetCreatedAt() {
        assertEquals("2024-10-01", item.getCreatedAt());
    }

    @Test
    public void testSetCreatedAt() {
        item.setCreatedAt("2024-11-01");
        assertEquals("2024-11-01", item.getCreatedAt());
    }

    @Test
    public void testGetParentCategoryId() {
        item.setParentCategoryId("cat123");
        assertEquals("cat123", item.getParentCategoryId());
    }

    @Test
    public void testSetParentCategoryId() {
        item.setParentCategoryId("cat456");
        assertEquals("cat456", item.getParentCategoryId());
    }

    @Test
    public void testGetSubCategoryId() {
        item.setSubCategoryId("sub123");
        assertEquals("sub123", item.getSubCategoryId());
    }

    @Test
    public void testSetSubCategoryId() {
        item.setSubCategoryId("sub456");
        assertEquals("sub456", item.getSubCategoryId());
    }

    @Test
    public void testGetParentCategoryName() {
        item.setParentCategoryName("Electronics");
        assertEquals("Electronics", item.getParentCategoryName());
    }

    @Test
    public void testSetParentCategoryName() {
        item.setParentCategoryName("Furniture");
        assertEquals("Furniture", item.getParentCategoryName());
    }

    @Test
    public void testGetSubcategoryName() {
        item.setSubcategoryName("Laptops");
        assertEquals("Laptops", item.getSubcategoryName());
    }

    @Test
    public void testSetSubcategoryName() {
        item.setSubcategoryName("Desktops");
        assertEquals("Desktops", item.getSubcategoryName());
    }

    @Test
    public void testGetExpiryDate() {
        item.setExpiryDate("2025-12-31");
        assertEquals("2025-12-31", item.getExpiryDate());
    }

    @Test
    public void testSetExpiryDate() {
        item.setExpiryDate("2026-01-01");
        assertEquals("2026-01-01", item.getExpiryDate());
    }
}
