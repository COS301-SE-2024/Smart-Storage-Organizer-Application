package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryReportModelTest {

    private CategoryReportModel categoryReport;
    private List<SubcategoryReportModel> subCategories;

    @Before
    public void setUp() {
        subCategories = new ArrayList<>();
        subCategories.add(new SubcategoryReportModel("Sub1", 10));
        subCategories.add(new SubcategoryReportModel("Sub2", 20));
        categoryReport = new CategoryReportModel("ParentCategory", subCategories, 30);
    }

    @Test
    public void testGetParentCategory() {
        assertEquals("ParentCategory", categoryReport.getParentCategory());
    }

    @Test
    public void testSetParentCategory() {
        categoryReport.setParentCategory("NewParentCategory");
        assertEquals("NewParentCategory", categoryReport.getParentCategory());
    }

    @Test
    public void testGetSubCategories() {
        assertEquals(subCategories, categoryReport.getSubCategories());
    }

    @Test
    public void testSetSubCategories() {
        List<SubcategoryReportModel> newSubCategories = new ArrayList<>();
        newSubCategories.add(new SubcategoryReportModel("Sub3", 15));
        categoryReport.setSubCategories(newSubCategories);
        assertEquals(newSubCategories, categoryReport.getSubCategories());
    }

    @Test
    public void testGetTotalNumberOfItems() {
        assertEquals(30, categoryReport.getTotalNumberOfItems(), 0);
    }

    @Test
    public void testSetTotalNumberOfItems() {
        categoryReport.setTotalNumberOfItems(50);
        assertEquals(50, categoryReport.getTotalNumberOfItems(), 0);
    }

    @Test
    public void testGetPercentage() {
        assertEquals(50, categoryReport.getPercentage(60), 0);
    }

//    @Test
//    public void testGetTotalQuantity() {
//        assertEquals(30, categoryReport.getTotalQuantity(), 0);
//    }
}
