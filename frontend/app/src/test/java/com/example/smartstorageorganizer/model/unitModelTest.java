package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class unitModelTest {

    private unitModel unit;

    @Before
    public void setUp() {
        unit = new unitModel("Storage Unit 1", "unit123", 100, 50, "Electronics, Furniture");
    }

    @Test
    public void testGetUnitName() {
        assertEquals("Storage Unit 1", unit.getUnitName());
    }

    @Test
    public void testGetId() {
        assertEquals("unit123", unit.getId());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(100, unit.getCapacity());
    }

    @Test
    public void testGetFreeCapacity() {
        assertEquals(50, unit.getFreeCapacity());
    }

    @Test
    public void testGetCapacityAsString() {
        assertEquals("Capacity: 100", unit.getCapacityAsString());
    }

    @Test
    public void testGetCapacityUsedAsString() {
        assertEquals("Capacity Used: 50", unit.getCapacityUsedAsString());
    }

    @Test
    public void testGetCategories() {
        List<String> categories = Arrays.asList("Electronics", "Furniture");
        unit.setCategories(categories);
        assertEquals(categories, unit.getCategories());
    }

    @Test
    public void testHasCategories() {
        List<String> categories = Arrays.asList("Electronics", "Furniture");
        unit.setCategories(categories);
        assertTrue(unit.hasCategories());
    }

    @Test
    public void testHasNoCategories() {
        unit.setCategories(null);
        assertFalse(unit.hasCategories());
    }

    @Test
    public void testIsExpanded() {
        assertFalse(unit.isExpanded());
    }

    @Test
    public void testSetExpanded() {
        unit.setExpanded(true);
        assertTrue(unit.isExpanded());
    }

    @Test
    public void testGetCategoriesString() {
        assertEquals("Electronics, Furniture", unit.getCategoriesString());
    }

    @Test
    public void testSetCategoriesString() {
        unit.setCategoriesString("Books, Clothing");
        assertEquals("Books, Clothing", unit.getCategoriesString());
    }
}
