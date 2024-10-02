package com.example.smartstorageorganizer.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AppReportModelTest {

    private AppReportModel appReportModel;

    @Before
    public void setUp() {
        // Initialize the AppReportModel with sample data
        appReportModel = new AppReportModel(
                "Dashboard",
                "100",
                "50",
                "2",
                "5m",
                "20"
        );
    }

    @Test
    public void testConstructor() {
        // Test if the constructor sets the values correctly
        assertEquals("Dashboard", appReportModel.getPageTitle());
        assertEquals("100", appReportModel.getViews());
        assertEquals("50", appReportModel.getActiveUsers());
        assertEquals("2", appReportModel.getViewsPerActiveUser());
        assertEquals("5m", appReportModel.getAverageEngagementTimePerActiveUser());
        assertEquals("20", appReportModel.getEventCount());
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getter and setter for pageTitle
        appReportModel.setPageTitle("HomePage");
        assertEquals("HomePage", appReportModel.getPageTitle());

        // Test the getter and setter for views
        appReportModel.setViews("200");
        assertEquals("200", appReportModel.getViews());

        // Test the getter and setter for activeUsers
        appReportModel.setActiveUsers("75");
        assertEquals("75", appReportModel.getActiveUsers());

        // Test the getter and setter for viewsPerActiveUser
        appReportModel.setViewsPerActiveUser("3");
        assertEquals("3", appReportModel.getViewsPerActiveUser());

        // Test the getter and setter for averageEngagementTimePerActiveUser
        appReportModel.setAverageEngagementTimePerActiveUser("10m");
        assertEquals("10m", appReportModel.getAverageEngagementTimePerActiveUser());

        // Test the getter and setter for eventCount
        appReportModel.setEventCount("30");
        assertEquals("30", appReportModel.getEventCount());
    }
}
