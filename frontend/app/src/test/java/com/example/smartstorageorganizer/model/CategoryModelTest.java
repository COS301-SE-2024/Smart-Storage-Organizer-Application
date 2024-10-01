package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CategoryModelTest {

    private CategoryModel category;

    @Before
    public void setUp() {
        category = new CategoryModel("Electronics", "123", "http://example.com/image.jpg");
    }

    @Test
    public void testGetCategoryName() {
        assertEquals("Electronics", category.getCategoryName());
    }

    @Test
    public void testSetCategoryName() {
        category.setCategoryName("Books");
        assertEquals("Books", category.getCategoryName());
    }

    @Test
    public void testGetCategoryID() {
        assertEquals("123", category.getCategoryID());
    }

    @Test
    public void testSetCategoryID() {
        category.setCategoryID("456");
        assertEquals("456", category.getCategoryID());
    }

    @Test
    public void testGetImageUrl() {
        assertEquals("http://example.com/image.jpg", category.getImageUrl());
    }

    @Test
    public void testSetImageUrl() {
        category.setImageUrl("http://example.com/newimage.jpg");
        assertEquals("http://example.com/newimage.jpg", category.getImageUrl());
    }

    @Test
    public void testGetParentCategoryId() {
        category.setParentCategoryId("789");
        assertEquals("789", category.getParentCategoryId());
    }

    @Test
    public void testSetParentCategoryId() {
        category.setParentCategoryId("101112");
        assertEquals("101112", category.getParentCategoryId());
    }
}
