package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SuggestedCategoryModelTest {

    private SuggestedCategoryModel suggestedCategory;

    @Before
    public void setUp() {
        suggestedCategory = new SuggestedCategoryModel();
        suggestedCategory.setItemId("item123");
        suggestedCategory.setCategoryId("cat456");
        suggestedCategory.setCategoryName("Electronics");
        suggestedCategory.setSubcategoryId("sub789");
        suggestedCategory.setSubcategoryName("Laptops");
    }

    @Test
    public void testGetItemId() {
        assertEquals("item123", suggestedCategory.getItemId());
    }

    @Test
    public void testSetItemId() {
        suggestedCategory.setItemId("item456");
        assertEquals("item456", suggestedCategory.getItemId());
    }

    @Test
    public void testGetCategoryId() {
        assertEquals("cat456", suggestedCategory.getCategoryId());
    }

    @Test
    public void testSetCategoryId() {
        suggestedCategory.setCategoryId("cat789");
        assertEquals("cat789", suggestedCategory.getCategoryId());
    }

    @Test
    public void testGetCategoryName() {
        assertEquals("Electronics", suggestedCategory.getCategoryName());
    }

    @Test
    public void testSetCategoryName() {
        suggestedCategory.setCategoryName("Furniture");
        assertEquals("Furniture", suggestedCategory.getCategoryName());
    }

    @Test
    public void testGetSubcategoryId() {
        assertEquals("sub789", suggestedCategory.getSubcategoryId());
    }

    @Test
    public void testSetSubcategoryId() {
        suggestedCategory.setSubcategoryId("sub123");
        assertEquals("sub123", suggestedCategory.getSubcategoryId());
    }

    @Test
    public void testGetSubcategoryName() {
        assertEquals("Laptops", suggestedCategory.getSubcategoryName());
    }

    @Test
    public void testSetSubcategoryName() {
        suggestedCategory.setSubcategoryName("Desktops");
        assertEquals("Desktops", suggestedCategory.getSubcategoryName());
    }
}
