package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubcategoryReportModelTest {

    private SubcategoryReportModel subcategoryReport;

    @Before
    public void setUp() {
        subcategoryReport = new SubcategoryReportModel("Electronics", 50);
        subcategoryReport.setTotalQuantity(100);
    }

    @Test
    public void testGetSubcategory() {
        assertEquals("Electronics", subcategoryReport.getSubcategory());
    }

    @Test
    public void testSetSubcategory() {
        subcategoryReport.setSubcategory("Books");
        assertEquals("Books", subcategoryReport.getSubcategory());
    }

    @Test
    public void testGetTotalNumberOfItems() {
        assertEquals(50, subcategoryReport.getTotalNumberOfItems());
    }

    @Test
    public void testSetTotalNumberOfItems() {
        subcategoryReport.setTotalNumberOfItems(75);
        assertEquals(75, subcategoryReport.getTotalNumberOfItems());
    }

    @Test
    public void testGetPercentage() {
        assertEquals(50.0, subcategoryReport.getPercentage(100), 0.01);
    }

    @Test
    public void testGetTotalQuantity() {
        assertEquals(100, subcategoryReport.getTotalQuantity());
    }

    @Test
    public void testSetTotalQuantity() {
        subcategoryReport.setTotalQuantity(150);
        assertEquals(150, subcategoryReport.getTotalQuantity());
    }
}
