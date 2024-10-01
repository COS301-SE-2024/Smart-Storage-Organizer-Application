package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginReportsModelTest {

    private LoginReportsModel loginReport;

    @Before
    public void setUp() {
        loginReport = new LoginReportsModel();
        loginReport.setName("John");
        loginReport.setSurname("Doe");
        loginReport.setTimeIn("08:00");
        loginReport.setTimeOut("17:00");
        loginReport.setEmail("john.doe@example.com");
    }

    @Test
    public void testGetName() {
        assertEquals("John", loginReport.getName());
    }

    @Test
    public void testSetName() {
        loginReport.setName("Jane");
        assertEquals("Jane", loginReport.getName());
    }

    @Test
    public void testGetSurname() {
        assertEquals("Doe", loginReport.getSurname());
    }

    @Test
    public void testSetSurname() {
        loginReport.setSurname("Smith");
        assertEquals("Smith", loginReport.getSurname());
    }

    @Test
    public void testGetTimeIn() {
        assertEquals("08:00", loginReport.getTimeIn());
    }

    @Test
    public void testSetTimeIn() {
        loginReport.setTimeIn("09:00");
        assertEquals("09:00", loginReport.getTimeIn());
    }

    @Test
    public void testGetTimeOut() {
        assertEquals("17:00", loginReport.getTimeOut());
    }

    @Test
    public void testSetTimeOut() {
        loginReport.setTimeOut("18:00");
        assertEquals("18:00", loginReport.getTimeOut());
    }

    @Test
    public void testGetEmail() {
        assertEquals("john.doe@example.com", loginReport.getEmail());
    }

    @Test
    public void testSetEmail() {
        loginReport.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", loginReport.getEmail());
    }
}
