package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReportModelTest {

    private ReportModel report;

    @Before
    public void setUp() {
        report = new ReportModel("Monthly Report", "This is a monthly report.");
    }

    @Test
    public void testGetName() {
        assertEquals("Monthly Report", report.getName());
    }

    @Test
    public void testSetName() {
        report.setName("Weekly Report");
        assertEquals("Weekly Report", report.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("This is a monthly report.", report.getDescription());
    }

    @Test
    public void testSetDescription() {
        report.setDescription("This is a weekly report.");
        assertEquals("This is a weekly report.", report.getDescription());
    }
}
